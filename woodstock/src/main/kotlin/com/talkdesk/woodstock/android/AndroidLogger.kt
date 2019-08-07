package com.talkdesk.woodstock.android

import android.util.Log
import com.talkdesk.woodstock.Logger

/**
 * Implementation of [Logger] abstraction using android's [Log].
 * @param internalLogsEnabled Controls if logging is enabled.
 * @param customerLogEnabled Controls if internal debug logs are enabled.
 * @param chunkSize Android [Log] has a limitation of String characters so if the message is bigger than this limit it will be partially printed.
 * To solve this we need to split the message into chunks. This is the chunk size in number of characters.
 */
open class AndroidLogger(
    private val customerLogEnabled: Boolean,
    private val internalLogsEnabled: Boolean,
    private val chunkSize: Int
) : Logger() {
    override fun log(tag: String, level: LogLevel, message: String) {
        if (customerLogEnabled && level == LogLevel.CUSTOMER) {
            val messageChunks = messageToChunks(message)
            messageChunks.forEach { Log.i(tag, it) }

            return
        }

        if (!internalLogsEnabled) return

        val messageChunks = messageToChunks(message)

        when (level) {
            LogLevel.ERROR -> messageChunks.forEach { Log.e(tag, it) }
            LogLevel.TRACE -> messageChunks.forEach { Log.d(tag, it) }
            LogLevel.NETWORK -> messageChunks.forEach { Log.d(tag, it) }
            LogLevel.CUSTOMER -> messageChunks.forEach { Log.i(tag, it) }
        }
    }

    override fun setup() {
        log("TEST", LogLevel.TRACE, "Android Logger setup. Customer logs enabled: $customerLogEnabled, internal logs enabled: $internalLogsEnabled and chunk size: $chunkSize")
    }

    private fun messageToChunks(message: String): MutableList<String> {
        val length = message.length

        if (length <= chunkSize) return mutableListOf(message)

        val chunks = mutableListOf<String>()
        var position = 0
        while (position < length) {
            var newline = message.indexOf('\n', position)
            newline = if (newline != -1) newline else length
            do {
                val end = Math.min(newline, position + chunkSize)
                val part = message.substring(position, end)
                chunks.add(part)
                position = end
            } while (position < newline)
            position++
        }

        return chunks
    }
}
