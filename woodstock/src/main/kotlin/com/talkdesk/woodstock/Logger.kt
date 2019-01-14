package com.talkdesk.woodstock

import java.io.ByteArrayOutputStream
import java.io.PrintStream

/**
 * Contract that should be respected by any logger.
 */
abstract class Logger {
    /**
     * Log a message with the specific log level.
     * @param level log level of the log.
     * @param message message of the log.
     */
    abstract fun log(level: LogLevel, message: String)

    /**
     * Log an exception's stacktrace.
     * @param level log level of the log.
     * @param exception exception to be logged.
     */
    open fun log(level: LogLevel, exception: Throwable) {
        val outStream = ByteArrayOutputStream()
        outStream.use {
            val printStream = PrintStream(outStream)
            printStream.use {
                exception.printStackTrace(printStream)
            }

            log(level, outStream.toString())
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
