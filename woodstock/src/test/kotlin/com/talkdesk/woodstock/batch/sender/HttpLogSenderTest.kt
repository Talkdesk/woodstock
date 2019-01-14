package com.talkdesk.woodstock.batch.sender

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.batch.Log
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class HttpLogSenderTest : Spek({

    describe("HTTP Log Sender") {
        it("should send log to a remote server via HTTP") {
            val server = MockWebServer()
            server.start()
            server.enqueue(MockResponse().setResponseCode(204))

            val extraData = mapOf(
                "appId" to "AppId",
                "osVersion" to "24",
                "deviceName" to "Samsung S9",
                "sdkVersion" to "0.0.4",
                "logVersion" to "0.0.1"
            )

            val jsonConverterMock = mock<LogJsonConverter>()
            val logSender = HttpLogSender.Builder()
                .setBaseUrl(server.url("/").toString())
                .setExtraDataProvider(object : ExtraDataProvider {
                    override fun getExtraData(): Map<String, String> = extraData
                })
                .build()

            whenever(jsonConverterMock.convertToJson(
                "2018-04-19 17:48:42.088",
                Logger.LogLevel.CUSTOMER,
                "Log message.",
                extraData
            )).thenReturn("{}")

            logSender.send(Log(
                "1",
                Logger.LogLevel.CUSTOMER,
                "Log message.",
                "2018-04-19 17:48:42.088")
            )

            server.shutdown()
        }
    }
})
