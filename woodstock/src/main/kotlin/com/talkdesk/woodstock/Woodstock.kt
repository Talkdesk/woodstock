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
