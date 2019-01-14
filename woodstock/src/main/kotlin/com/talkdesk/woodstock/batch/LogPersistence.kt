package com.talkdesk.woodstock.batch

import com.talkdesk.woodstock.Logger

/**
 * Manages log persistence.
 */
interface LogPersistence {

    /**
     * Create and persist log.
     * @param logLevel the log level.
     * @param message the log message.
     * @param timestamp the log generation timestamp.
     */
    fun save(logLevel: Logger.LogLevel, message: String, timestamp: String)

    /**
     * Returns persisted logs.
     */
    fun getLogs(): List<Log>

    /**
     * Remove persisted log.
     * @param logId the log id.
     */
    fun remove(logId: String)
}
