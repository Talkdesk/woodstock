package com.talkdesk.woodstock.batch.sender

import com.talkdesk.woodstock.Logger

internal interface LogJsonConverter {
    /**
     * Serializes parameters to log messages. Parameters are not serialized in an specific order.
     * @param timestamp current time.
     * @param level log level.
     * @param message log message.
     * @param extraData extra key-value parameters.
     * @return JSON-encoded string.
     */
    fun convertToJson(
        timestamp: String,
        level: Logger.LogLevel,
        message: String,
        extraData: Map<String, String>
    ): String
}
