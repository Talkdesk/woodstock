package com.talkdesk.woodstock

import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Contract that should be respected by any logger.
 */
abstract class Logger {
    /**
     * Log a message with the specific log level.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param level log level of the log.
     * @param message message of the log.
     */
    abstract fun log(tag: String, level: LogLevel, message: String)

    /**
     * Log an exception's stacktrace.
     * @param tag Used to identify the source of a log message. It usually identifies the class or activity where the log call occurs.
     * @param level log level of the log.
     * @param exception exception to be logged.
     */
    open fun log(tag: String, level: LogLevel, exception: Throwable) {
        val outStream = ByteArrayOutputStream()
        outStream.use {
            val printStream = PrintStream(outStream)
            printStream.use {
                exception.printStackTrace(printStream)
            }

            log(tag, level, outStream.toString())
        }
    }

    /**
     * Setups logger.
     */
    abstract fun setup()

    enum class LogLevel {
        CUSTOMER,
        ERROR,
        TRACE,
        NETWORK
    }
}
