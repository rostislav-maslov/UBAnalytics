package com.unitbean.analytics.transport

internal class MockTracker(private val sessionId: String) : Tracker {

    override suspend fun initSession(projectId: String, deviceId: String): Any = "Ok"

    override suspend fun logEvent(tag: String, params: Map<String, Any>?): Any = "Ok"

    override suspend fun setParams(tag: String, params: Map<String, Any>?): Any = "Ok"
}