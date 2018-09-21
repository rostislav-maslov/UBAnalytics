package com.unitbean.analytics.transport

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async

internal class MockTransport(private val sessionId: String) : Transport {

    override fun initSession(projectId: String): Deferred<Any> = GlobalScope.async { "Ok" }

    override fun logEvent(tag: String, params: Map<String, Any>?): Deferred<Any> = GlobalScope.async { "Ok" }

    override fun screenOpen(name: String, params: Map<String, Any>?): Deferred<Any> = GlobalScope.async { "Ok" }
}