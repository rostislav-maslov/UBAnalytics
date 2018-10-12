package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.unitbean.analytics.transport.HttpTracker
import com.unitbean.analytics.transport.Tracker
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import kotlin.collections.HashMap
import kotlin.coroutines.experimental.CoroutineContext

@Suppress("UNUSED")
object UBAnalytics : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private const val DEVICE_ID = "device_id"
    private const val GOTO = "GoTo"

    private val tracker: Tracker by lazy { HttpTracker(projectKey) }

    private var deviceId: String? = null
    private var sessionId: String? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var activityTracker: ActivityTracker
    private lateinit var projectKey: String

    /**
     * Инициализация аналитики
     * @param context - контекст уровня [Application]
     * @param projectId - ключ проекта для ассоциации
     */
    fun init(context: Application, projectId: String) {
        if (projectId.trim().isEmpty()) {
            throw IllegalArgumentException("ProjectId cannot be empty")
        }

        preferences = context.getSharedPreferences(projectId, Context.MODE_PRIVATE)

        activityTracker = ActivityTracker(context, object : ActivityCallback {
            override fun onActivityStart(activity: Activity?) {
                logEvent(GOTO, "Screen" to (activity?.localClassName ?: "Name activity not found"))
            }
        })

        activityTracker.startTracking()

        projectKey = projectId

        launch {
            try {
                deviceId = preferences.getString(DEVICE_ID, null)
                val session = tracker.initSession(deviceId)
                deviceId = session.result.deviceId
                sessionId = session.result.sessionId

                preferences.edit().putString(DEVICE_ID, deviceId).apply()
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Логгирует кастомный ивент пользователя
     */
    fun logEvent(tag: String, params: Map<String, Any>) {
        launch {
            try {
                sessionId?.let {
                    tracker.logEvent(tag, it, params)
                }
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Pair]
     */
    fun logEvent(tag: String, value: Pair<String, Any>) {
        logEvent(tag, mapOf(value))
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Bundle]
     */
    fun logEvent(tag: String, params: Bundle) {
        logEvent(tag, HashMap<String, Any>().apply {
            for (key in params.keySet()) {
                this[key] = params[key] ?: continue
            }
        })
    }
}