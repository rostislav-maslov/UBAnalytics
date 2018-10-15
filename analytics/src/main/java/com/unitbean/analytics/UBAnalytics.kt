package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.unitbean.analytics.transport.HttpTracker
import com.unitbean.analytics.transport.Tracker
import com.unitbean.analytics.transport.TrackerTypes
import com.unitbean.analytics.transport.TypeTypes
import com.unitbean.analytics.transport.models.ActionRequest
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
    private const val SESSION_ID = "session_id"

    var isDebuggable = false

    private val tracker: Tracker by lazy { HttpTracker(projectKey) }

    private var deviceId: String? = null
    private var sessionId: String? = null

    private lateinit var preferences: SharedPreferences
    private lateinit var activityTracker: ActivityTracker
    private lateinit var projectKey: String

    /**
     * Инициализация аналитики
     * @param context - контекст уровня [Application]
     * @param projectToken - токен проекта для ассоциации
     * @param clientVersion - версия клиента для отсылания
     */
    suspend fun init(context: Application, projectToken: String, clientVersion: String) {
        if (projectToken.trim().isEmpty()) {
            throw IllegalArgumentException("ProjectToken cannot be empty")
        }

        preferences = context.getSharedPreferences(projectToken, Context.MODE_PRIVATE)

        activityTracker = ActivityTracker(context, object : ActivityCallback {
            override fun onActivityStart(activity: Activity?) {
                logEvent(TypeTypes.GO_TO.name, "SCREEN" to (activity?.localClassName ?: "Name activity not found"))
            }
        })

        activityTracker.startTracking()

        projectKey = projectToken

        deviceId = preferences.getString(DEVICE_ID, null)
        val session = tracker.initSession(deviceId, clientVersion)
        deviceId = session.result.deviceId
        sessionId = session.result.sessionId

        preferences.edit()
            .putString(DEVICE_ID, deviceId)
            .apply()
    }

    /**
     * Логгирует кастомный ивент пользователя
     */
    fun logEvent(tag: String, params: Map<String, Any>) {
        launch {
            try {
                sessionId?.let {
                    tracker.logEvent(tag, it, params.mapValues { entry ->
                        ActionRequest.CustomField(entry.value, TrackerTypes.getType(entry.value))
                    })
                }
            } catch (e: Exception) {
                if (isDebuggable) {
                    Log.e("UBAnalytics", e.message, e)
                }
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

    /**
     * Запрос на сохранение данных пользователя
     */
    fun register(externalId: String, params: Map<String, Any>) {
        launch {
            try {
                if (!sessionId.isNullOrEmpty()) {
                    tracker.userRegister(externalId, sessionId!!, params.mapValues { entry ->
                        ActionRequest.CustomField(entry.key, TrackerTypes.getType(entry.value))
                    })
                }
            } catch (e: Exception) {
                if (isDebuggable) {
                    Log.e("UBAnalytics", e.message, e)
                }
            }
        }
    }

    /**
     * Запрос на сохранение данных UTM меток для сессии
     * Используется для трекинга маркетинговых кампаний
     */
    fun utmSession(source: String, medium: String, campaign: String, content: String, term: String) {
        launch {
            try {
                if (!sessionId.isNullOrEmpty()) {
                    tracker.utmSession(sessionId!!, source, medium, campaign, content, term)
                }
            } catch (e: Exception) {
                if (isDebuggable) {
                    Log.e("UBAnalytics", e.message, e)
                }
            }
        }
    }
}