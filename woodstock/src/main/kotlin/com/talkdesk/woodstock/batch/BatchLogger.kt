package com.talkdesk.woodstock.batch

import android.annotation.SuppressLint
import android.content.Context
import android.os.HandlerThread
import com.talkdesk.woodstock.BuildConfig
import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.Woodstock
import com.talkdesk.woodstock.batch.persistence.LogSQLiteOpenHelper
import com.talkdesk.woodstock.batch.persistence.SQLiteLogPersistence
import com.talkdesk.woodstock.batch.sender.ExtraDataProvider
import com.talkdesk.woodstock.batch.sender.HttpLogSender
import com.talkdesk.woodstock.batch.sender.JsonObjectLogConverter
import com.talkdesk.woodstock.batch.time.SystemTimeGenerator
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.MediaType
import okhttp3.OkHttpClient

/**
 * [Logger] implementation that sends logs in batch.
 * @param logPersistence persists logs.
 * @param logSender sends logs.
 * @param scheduler sets scheduler where [LogPersistence] and [LogSender] calls will be performed.
 * @param enabled whether this logger is enabled or not.
 * @param timeGenerator generates the timestamp when the log was generated.
 * @param internalLogger logger to log internal information about this [BatchLogger]. It should not be an instance of
 * [BatchLogger] otherwise this instance could end up in an inconsistent state.
 */
@SuppressLint("CheckResult")
class BatchLogger internal constructor(
    private val logPersistence: LogPersistence,
    private val logSender: LogSender,
    private val scheduler: Scheduler,
    private val enabled: Boolean,
    private val timeGenerator: TimeGenerator,
    private val internalLogger: Logger,
    private val threshold: Int,
    private val lock: Any = Any()
) : Logger() {

    override fun log(tag: String, level: LogLevel, message: String) {
        if (!enabled || level == LogLevel.NETWORK) {
            return
        }

        Completable.fromAction {
            synchronized(lock) {
                logPersistence.save(level, message, timeGenerator.currentTimestamp())
                sendPersistedLogs(threshold)
            }
        }.subscribeOn(scheduler)
            .subscribe({
                internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "Persisted log with level: $level and message: $message")
            }, { exception ->
                internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.ERROR, exception)
            })
    }

    override fun setup() {
        internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "Batch Logger setup. Enabled: $enabled")
        Completable.fromAction {
            synchronized(lock) {
                sendPersistedLogs(logPersistence.getLogs())
            }
        }.subscribeOn(scheduler)
            .subscribe({}, { exception ->
                internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.ERROR, exception)
            })
    }

    private fun sendPersistedLogs(threshold: Int) {
        val logs = logPersistence.getLogs()
        if (logs.size >= threshold) {
            internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "${logs.size} logs persisted. $threshold threshold reached.")
            sendPersistedLogs(logs)
        }
    }

    private fun sendPersistedLogs(logs: List<Log>) {

        if (logs.isEmpty()) {
            internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "No persisted logs to send.")
            return
        }

        internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "Sending persisted logs via log sender.")
        for (log in logs) {
            logSender.send(log)
            internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "Sent log with id: ${log.id}, level: ${log.level} and message: ${log.message}")
            logPersistence.remove(log.id)
            internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "Removed log with id: ${log.id}")
        }
        internalLogger.log(Woodstock.DEFAULT_TAG, LogLevel.TRACE, "All persisted logs sent successfully.")
    }

    /**
     * Builds [BatchLogger] instance.
     */
    class Builder(private val context: Context) {

        private var scheduler: Scheduler? = null
        private var baseUrl: String? = null
        private var extraDataProvider: ExtraDataProvider? = null
        private var enabled: Boolean = true
        private var threshold: Int = BuildConfig.REMOTE_LOG_THRESHOLD
        private var internalLogger: Logger = object : Logger() {
            override fun log(tag: String, level: LogLevel, message: String) {
            }

            override fun setup() {
            }
        }

        /**
         * Sets the base url of the remote API (e.g. http://www.example.com:8888/).
         */
        fun setBaseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        /**
         * Sets extra data to be sent along with logs.
         */
        fun setExtraDataProvider(extraDataProvider: ExtraDataProvider): Builder {
            this.extraDataProvider = extraDataProvider
            return this
        }

        fun setEnabled(enabled: Boolean): Builder {
            this.enabled = enabled
            return this
        }

        fun setThreshold(threshold: Int): Builder {
            this.threshold = threshold
            return this
        }

        fun setInternalLogger(logger: Logger): Builder {
            this.internalLogger = logger
            return this
        }

        fun setScheduler(scheduler: Scheduler): Builder {
            this.scheduler = scheduler
            return this
        }

        /**
         * Validates the values and returns a new [HttpLogSender] instance.
         */
        fun build(): BatchLogger {

            if (baseUrl == null) {
                throw IllegalStateException("Missing base url.")
            }

            if (extraDataProvider == null) {
                throw IllegalStateException("Missing extra data provider.")
            }

            if (scheduler == null) {
                val thread = HandlerThread("BATCH_LOGGER")
                thread.start()
                scheduler = AndroidSchedulers.from(thread.looper)
            }

            val logSender = HttpLogSender(
                httpClient = OkHttpClient(),
                baseUrl = baseUrl!!,
                mediaType = MediaType.parse("application/json; charset=utf-8")!!,
                jsonConverter = JsonObjectLogConverter(),
                extraDataProvider = extraDataProvider!!
            )
            val logPersistence = SQLiteLogPersistence(LogSQLiteOpenHelper(context, BuildConfig.DATABASE_FILE_NAME, BuildConfig.DATABASE_VERSION))

            return BatchLogger(logPersistence, logSender, scheduler!!, enabled, SystemTimeGenerator(), internalLogger, threshold)
        }
    }
}
