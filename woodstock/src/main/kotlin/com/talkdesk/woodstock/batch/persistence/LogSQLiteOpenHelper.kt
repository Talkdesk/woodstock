package com.talkdesk.woodstock.batch.persistence

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Manages log database and related tables.
 * @param context application context.
 * @param name database file name.
 * @param version database version.
 */
internal class LogSQLiteOpenHelper(context: Context, name: String?, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    /**
     * Information about the table where logs are stored.
     */
    object LogTable {

        /**
         * Log table name.
         */
        const val NAME = "log"
        /**
         * Log table id column.
         */
        const val COLUMN_ID = BaseColumns._ID
        /**
         * Log table level column.
         */
        const val COLUMN_LEVEL = "level"
        /**
         * Log table message column.
         */
        const val COLUMN_MESSAGE = "message"
        /**
         * Log table timestamp column.
         */
        const val COLUMN_TIMESTAMP = "timestamp"

        /**
         * Log table creation SQL.
         */
        const val CREATE =
            "CREATE TABLE $NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_LEVEL TEXT NOT NULL," +
                "$COLUMN_MESSAGE TEXT NOT NULL," +
                "$COLUMN_TIMESTAMP TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(LogTable.CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // No updates yet. If there is a change of schema we should treat it here.
    }
}
