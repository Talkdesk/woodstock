package com.talkdesk.woodstock.batch.persistence

import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.batch.Log
import com.talkdesk.woodstock.batch.LogPersistence

/**
 * [LogPersistence] implementation that persists logs in memory.
 * @param logs a map of log id to [Log] instance pre-loaded in memory. Log ids should be consistent with
 * last generated id. Empty by default.
 * @param lastGeneratedId incremented when a new log is created and decremented when an existing log is removed.
 * 0 by default.
 */
internal class FakeLogPersistence(
    private val logs: MutableMap<Int, Log> = mutableMapOf(),
    private var lastGeneratedId: Int = 0
) : LogPersistence {

    override fun save(logLevel: Logger.LogLevel, message: String, timestamp: String) {
        logs[lastGeneratedId] = Log("$lastGeneratedId", logLevel, message, timestamp)
        lastGeneratedId++
    }

    override fun getLogs(): List<Log> {
        return ArrayList(logs.values)
    }

    override fun remove(logId: String) {
        logs.remove(logId.toInt())
        lastGeneratedId--
    }
}
