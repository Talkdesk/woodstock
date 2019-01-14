package com.talkdesk.woodstock.batch

/**
 * TimeGenerator that provides time related functions.
 */
interface TimeGenerator {
    /**
     * Get current time.
     * @return current timestamp.
     */
    fun currentTimestamp(): String
}
