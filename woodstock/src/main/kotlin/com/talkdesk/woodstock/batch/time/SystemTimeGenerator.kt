package com.talkdesk.woodstock.batch.time

import com.talkdesk.woodstock.batch.TimeGenerator
import java.sql.Timestamp

/**
 * Implementation of [TimeGenerator] abstraction implemented with system's clock.
 */
internal class SystemTimeGenerator : TimeGenerator {
    override fun currentTimestamp(): String {
        return Timestamp(System.currentTimeMillis()).toString()
    }
}
