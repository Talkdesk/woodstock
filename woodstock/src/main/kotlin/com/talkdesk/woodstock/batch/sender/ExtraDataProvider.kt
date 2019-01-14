package com.talkdesk.woodstock.batch.sender

interface ExtraDataProvider {

    fun getExtraData(): Map<String, String>
}
