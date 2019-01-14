package com.talkdesk.woodstock.batch.persistence

import android.content.ContentValues
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.talkdesk.woodstock.Logger
import com.talkdesk.woodstock.batch.Log
import com.talkdesk.woodstock.batch.LogPersistence

/**
 * [LogPersistence] implementation that persists logs using SQLite.
 * @param openHelper Android database helper where [android.database.sqlite.SQLiteDatabase] instance can be obtained.
 */
internal class SQLiteLogPersistence(private val openHelper: SQLiteOpenHelper) : LogPersistence {

    override fun save(logLevel: Logger.LogLevel, message: String, timestamp: String) {
        val database = openHelper.writableDatabase
        val values = ContentValues().apply {
            put(LogSQLiteOpenHelper.LogTable.COLUMN_LEVEL, logLevel.name)
            put(LogSQLiteOpenHelper.LogTable.COLUMN_MESSAGE, message)
            put(LogSQLiteOpenHelper.LogTable.COLUMN_TIMESTAMP, timestamp)
        }

        if (database.insert(LogSQLiteOpenHelper.LogTable.NAME, null, values) == -1L) {
            throw SQLiteException("Could not insert log with level: ${logLevel.name} and message: $message")
        }
    }

    override fun getLogs(): List<Log> {
        val database = openHelper.readableDatabase

        val cursor = database.query(
            LogSQLiteOpenHelper.LogTable.NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        val logs = mutableListOf<Log>()
        cursor.use {
            while (it.moveToNext()) {
                logs.add(
                    Log(
                        it.getString(it.getColumnIndexOrThrow(LogSQLiteOpenHelper.LogTable.COLUMN_ID)),
                        Logger.LogLevel.valueOf(it.getString(it.getColumnIndexOrThrow(LogSQLiteOpenHelper.LogTable.COLUMN_LEVEL))),
                        it.getString(it.getColumnIndexOrThrow(LogSQLiteOpenHelper.LogTable.COLUMN_MESSAGE)),
                        it.getString(it.getColumnIndexOrThrow(LogSQLiteOpenHelper.LogTable.COLUMN_TIMESTAMP))
                    )
                )
            }
        }
        return logs
    }

    override fun remove(logId: String) {

        val database = openHelper.writableDatabase

        if (database.delete(LogSQLiteOpenHelper.LogTable.NAME, "${LogSQLiteOpenHelper.LogTable.COLUMN_ID} = ?", arrayOf(logId)) != 1) {
            throw SQLiteException("Failed to remove log with id: $logId")
        }
    }
}
