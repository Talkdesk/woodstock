package com.talkdesk.woodstock

/**
 * Implementation of [Logger] abstraction which redirects logs to a collection of loggers.
 * @param loggers collection of used loggers.
 */
class Woodstock private constructor(private val loggers: Array<Logger>) : Logger() {
    override fun log(level: LogLevel, message: String) {
        for (logger in loggers) {
            logger.log(level, message)
        }
    }

    override fun setup() {
        for (logger in loggers) {
            logger.setup()
        }
    }

    class Builder {

        private val loggers: MutableList<Logger> = mutableListOf()

        fun addLogger(logger: Logger): Builder {
            loggers.add(logger)
            return this
        }

        fun build(): Woodstock {

            if (loggers.isEmpty()) {
                throw IllegalStateException("Add at least one " + Logger::class.java.simpleName)
            }

            return Woodstock(loggers.toTypedArray())
        }
    }
}
