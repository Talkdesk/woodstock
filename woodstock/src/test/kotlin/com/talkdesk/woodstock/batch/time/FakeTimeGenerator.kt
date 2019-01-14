package com.talkdesk.woodstock.batch.time

import com.talkdesk.woodstock.batch.TimeGenerator

/**
 * [TimeGenerator] implementation that returns a fake timestamp.
 * @param timestamp fake timestamp to be returned.
 */
internal class FakeTimeGenerator(private val timestamp: String) : TimeGenerator {

    override fun currentTimestamp(): String {
        return timestamp
    }
}
