package com.talkdesk.woodstock.batch

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.batch.persistence.FakeLogPersistence
import com.talkdesk.woodstock.batch.time.FakeTimeGenerator
import io.reactivex.schedulers.Schedulers
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.assertTrue

class BatchLoggerTest : Spek({

    describe("Batch Logger") {

        it("should persist logs with timestamp when log is called to send in batch afterwords") {
            val mockLogPersistence = mock<LogPersistence>()
            val fakeTimeGenerator = FakeTimeGenerator("27/09/1988")
            val batchLogger = BatchLogger(mockLogPersistence, mock(), Schedulers.trampoline(), true, fakeTimeGenerator, mock(), 99)

            batchLogger.log("TEST", Logger.LogLevel.ERROR, "Error happened")

            verify(mockLogPersistence).save(Logger.LogLevel.ERROR, "Error happened", "27/09/1988")
        }

        it("should send logs from persistence in batch on setup") {

            val traceLog = Log("1", Logger.LogLevel.TRACE, "401 - Not Authorized", "timestamp")
            val errorLog = Log("2", Logger.LogLevel.ERROR, "Network error!", "timestamp")
            val customerLog = Log("3", Logger.LogLevel.CUSTOMER, "Talkdesk SDK started.", "timestamp")

            val fakeLogPersistence = FakeLogPersistence(mutableMapOf(
                1 to traceLog,
                2 to errorLog,
                3 to customerLog), 4)

            val mockLogSender = mock<LogSender>()
            val batchLogger = BatchLogger(fakeLogPersistence, mockLogSender, Schedulers.trampoline(), true, mock(), mock(), 99)
            batchLogger.setup()

            verify(mockLogSender).send(traceLog)
            verify(mockLogSender).send(errorLog)
            verify(mockLogSender).send(customerLog)

            assertTrue(fakeLogPersistence.getLogs().isEmpty())
        }

        it("should send logs when threshold is reached") {

            val mockLogSender = mock<LogSender>()
            val batchLogger = BatchLogger(
                FakeLogPersistence(),
                mockLogSender,
                Schedulers.trampoline(),
                true,
                FakeTimeGenerator("timestamp"),
                mock(),
                3
            )
            batchLogger.setup()
            batchLogger.log("TEST", Logger.LogLevel.TRACE, "401 - Not Authorized")
            verify(mockLogSender, never()).send(any())
            batchLogger.log("TEST", Logger.LogLevel.ERROR, "Network error!")
            verify(mockLogSender, never()).send(any())
            batchLogger.log("TEST", Logger.LogLevel.CUSTOMER, "Talkdesk SDK started.")

            verify(mockLogSender).send(Log("0", Logger.LogLevel.TRACE, "401 - Not Authorized", "timestamp"))
            verify(mockLogSender).send(Log("1", Logger.LogLevel.ERROR, "Network error!", "timestamp"))
            verify(mockLogSender).send(Log("2", Logger.LogLevel.CUSTOMER, "Talkdesk SDK started.", "timestamp"))
        }

        it("should not persist network logs") {

            val mockLogPersistence = mock<LogPersistence>()
            val batchLogger = BatchLogger(mockLogPersistence, mock(), Schedulers.trampoline(), true, FakeTimeGenerator("01/01/1900"), mock(), 99)

            batchLogger.log("TEST", Logger.LogLevel.NETWORK, "401 - Not Authorized")

            verify(mockLogPersistence, never()).save(Logger.LogLevel.NETWORK, "401 - Not Authorized", "01/01/1900")
        }

        it("should not persist logs when disabled") {
            val mockLogPersistence = mock<LogPersistence>()
            val batchLogger = BatchLogger(mockLogPersistence, mock(), Schedulers.trampoline(), false, FakeTimeGenerator("1234"), mock(), 99)

            batchLogger.log("TEST", Logger.LogLevel.ERROR, "Error happened")

            verify(mockLogPersistence, never()).save(eq(Logger.LogLevel.ERROR), eq("Error happened"), any())
        }
    }
})
