package com.talkdesk.woodstock.batch

/**
 * Sends logs.
 */
interface LogSender {

    /**
     * Sends log.
     * @param log to be sent.
     */
    fun send(log: Log)
}
