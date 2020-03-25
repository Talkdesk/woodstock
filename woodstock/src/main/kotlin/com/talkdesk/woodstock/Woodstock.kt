@file:Suppress("NOTHING_TO_INLINE")

package com.talkdesk.woodstock

/**
 * Implementation of [Logger] abstraction which redirects logs to a collection of loggers.
 */
class Woodstock private constructor() {

    companion object {

        const val DEFAULT_TAG = "Woodstock"

        private var loggers: Array<Logger> = emptyArray()

        fun log(tag: String, level: Logger.LogLevel, message: String) {
            for (logger in loggers) {
                logger.log(tag, level, message)
            }
        }

        fun log(tag: String, level: Logger.LogLevel, exception: Throwable) {
            for (logger in loggers) {
                logger.log(tag, level, exception)
            }
        }

        /** Send an ERROR log message. */
        inline fun e(tag: String, message: String) = log(tag, Logger.LogLevel.ERROR, message)

        /** Send a TRACE log message. */
        inline fun t(tag: String, message: String) = log(tag, Logger.LogLevel.TRACE, message)

        inline fun customer(tag: String, message: String) = log(tag, Logger.LogLevel.CUSTOMER, message)

        inline fun net(tag: String, message: String) = log(tag, Logger.LogLevel.NETWORK, message)

        /** Send an ERROR log message. */
        inline fun e(tag: String, exception: Throwable) = log(tag, Logger.LogLevel.ERROR, exception)

        /** Send a TRACE log message. */
        inline fun t(tag: String, exception: Throwable) = log(tag, Logger.LogLevel.TRACE, exception)

        inline fun customer(tag: String, exception: Throwable) = log(tag, Logger.LogLevel.CUSTOMER, exception)

        inline fun net(tag: String, exception: Throwable) = log(tag, Logger.LogLevel.NETWORK, exception)

        /***
         * Setup Woodstock.
         * @param loggers collection of used loggers.
         */
        fun setup(loggers: Array<Logger>) {

            Woodstock.loggers = loggers

            for (logger in loggers) {
                logger.setup()
            }
        }
    }
}
