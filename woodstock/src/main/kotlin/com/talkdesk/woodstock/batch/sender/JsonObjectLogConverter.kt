package com.talkdesk.woodstock.batch.sender

import com.talkdesk.woodstock.Logger
import org.json.JSONObject

internal class JsonObjectLogConverter : LogJsonConverter {

    override fun convertToJson(timestamp: String, level: Logger.LogLevel, message: String, extraData: Map<String, String>): String {
        val jsonObject = JSONObject()
        jsonObject.put("timestamp", timestamp)
        jsonObject.put("level", level)
        jsonObject.put("message", message)
        for (key in extraData.keys) {
            jsonObject.put(key, extraData[key])
        }

        return jsonObject.toString()
    }
}
