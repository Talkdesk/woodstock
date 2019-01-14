package com.talkdesk.woodstock.batch.persistence

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.batch.Log
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SQLiteLogPersistenceTest {

    @Test
    fun shouldReturnSavedLogs() {
        // Passing null to database name in LogSQLiteOpenHelper creates an in memory database to avoid flaky tests
        val logPersistence = SQLiteLogPersistence(LogSQLiteOpenHelper(InstrumentationRegistry.getTargetContext(), null, 1))

        logPersistence.save(Logger.LogLevel.CUSTOMER, "Talkdesk SDK started!", "timestamp-1")

        assertTrue(logPersistence.getLogs().contains(Log("1", Logger.LogLevel.CUSTOMER, "Talkdesk SDK started!", "timestamp-1")))
    }

    @Test
    fun shouldRemoveSavedLogs() {
        val logPersistence = SQLiteLogPersistence(LogSQLiteOpenHelper(InstrumentationRegistry.getTargetContext(), null, 1))

        logPersistence.save(Logger.LogLevel.CUSTOMER, "Talkdesk SDK started!", "timestamp")
        logPersistence.remove(logPersistence.getLogs()[0].id)

        assertTrue(logPersistence.getLogs().isEmpty())
    }
}
