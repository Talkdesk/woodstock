package com.talkdesk.woodstock.batch

import com.talkdesk.woodstock.Logger

/**
 * Represents a log entry.
 * @param id unique identifier of the log entry.
 * @param level the log level.
 * @param message the log message.
 * @param timestamp the log timestamp.
 */
data class Log(val id: String, val level: Logger.LogLevel, val message: String, val timestamp: String)
