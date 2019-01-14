package com.talkdesk.woodstock.batch.sender

import com.talkdesk.woodstock.batch.Log
import com.talkdesk.woodstock.batch.LogSender
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Implementation of [LogSender] which sends logs to the remote server via HTTP.
 * @param httpClient client that will be used to communicate with remote API via HTTP.
 * @param baseUrl scheme and authority of the remote API (e.g. http://www.example.com:8888/).
 * @param mediaType media type used to serialize the body of API requests (e.g. application/json; charset=utf-8).
 * @param jsonConverter converter to parse JSON into entity instances and serialize objects into JSON-encoded string.
 */
internal class HttpLogSender(
    private val httpClient: OkHttpClient,
    private val baseUrl: String,
    private val mediaType: MediaType,
    private val jsonConverter: LogJsonConverter,
    private val extraDataProvider: ExtraDataProvider
) : LogSender {

    override fun send(log: Log) {
        val request = Request.Builder()
            .url("${baseUrl}log")
            .post(RequestBody.create(mediaType, jsonConverter.convertToJson(log.timestamp, log.level, log.message, extraDataProvider.getExtraData())))
            .build()

        httpClient.newCall(request).execute()
    }

    /**
     * Builds [HttpLogSender] instance.
     */
    class Builder {

        private var baseUrl: String? = null
        private var extraDataProvider: ExtraDataProvider? = null

        /**
         * Sets the base url of the remote API (e.g. http://www.example.com:8888/).
         */
        fun setBaseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        /**
         * Sets the current log version.
         */
        fun setExtraDataProvider(extraDataProvider: ExtraDataProvider): Builder {
            this.extraDataProvider = extraDataProvider
            return this
        }

        /**
         * Validates the values and returns a new [HttpLogSender] instance.
         */
        fun build(): HttpLogSender {

            if (baseUrl == null) {
                throw IllegalStateException("Missing base url.")
            }

            if (extraDataProvider == null) {
                throw IllegalStateException("Missing extra data provider.")
            }

            return HttpLogSender(
                httpClient = OkHttpClient(),
                baseUrl = baseUrl!!,
                mediaType = MediaType.parse("application/json; charset=utf-8")!!,
                jsonConverter = JsonObjectLogConverter(),
                extraDataProvider = extraDataProvider!!
            )
        }
    }
}
