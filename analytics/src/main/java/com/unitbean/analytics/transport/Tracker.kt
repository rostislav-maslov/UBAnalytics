package com.unitbean.analytics.transport

internal interface Tracker {
    suspend fun initSession(projectId: String, deviceId: String): Any
    suspend fun setParams(tag: String, params: Map<String, Any>? = null): Any
    suspend fun logEvent(tag: String, params: Map<String, Any>? = null): Any
}