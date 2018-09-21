package com.unitbean.analytics.transport

import kotlinx.coroutines.experimental.Deferred

internal interface Transport {
    fun initSession(projectId: String): Deferred<Any>
    fun screenOpen(name: String, params: Map<String, Any>? = null): Deferred<Any>
    fun logEvent(tag: String, params: Map<String, Any>? = null): Deferred<Any>
}