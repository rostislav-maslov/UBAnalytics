package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.unitbean.analytics.transport.MockTransport
import com.unitbean.analytics.transport.Transport
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.HashMap

object UBAnalytics {

    private val sessionId by lazy { UUID.randomUUID().toString() }
    private val httpService: Transport by lazy { MockTransport(sessionId) /* HttpService(sessionId) */ }

    private lateinit var activityTracker: ActivityTracker

    private var projectKey: String? = null

    /**
     * Инициализация аналитики
     * @param context - контекст
     * @param projectId - ключ проекта для ассоциации
     */
    fun init(context: Context, projectId: String) {
        if (context !is Application) {
            throw IllegalStateException("Call .init on Application context instance")
        } else if (projectId.trim().isEmpty()) {
            throw IllegalStateException("ProjectId cannot be empty")
        }

        activityTracker = ActivityTracker(context, object : ActivityCallback {
            override fun onActivityStart(activity: Activity?) {
                GlobalScope.launch {
                    try {
                        httpService.screenOpen(activity?.localClassName ?: "Name activity not found").await()
                    } catch (e: Exception) {

                    }
                }
            }

            override fun onActivityDestroyed(activity: Activity?) {
                // unused yet
            }
        })

        activityTracker.startTracking()

        projectKey = projectId

        GlobalScope.launch {
            try {
                httpService.initSession(projectId).await()
            } catch (e: Exception) {

            }
        }
    }

    /**
     * Логгирует кастомный ивент пользователя
     */
    fun logEvent(tag: String, params: Map<String, Any>) = GlobalScope.launch {
        try {
            httpService.logEvent(tag, params).await()
        } catch (e: Exception) {

        }
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Pair]
     */
    fun logEvent(tag: String, value: Pair<String, Any>) = GlobalScope.launch {
        logEvent(tag, mapOf(value))
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Bundle]
     */
    fun logEvent(tag: String, params: Bundle) = GlobalScope.launch {
        logEvent(tag, HashMap<String, Any>().apply {
            for (key in params.keySet()) {
                this[key] = params[key] ?: continue
            }
        })
    }
}