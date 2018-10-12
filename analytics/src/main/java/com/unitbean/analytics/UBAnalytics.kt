package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import com.unitbean.analytics.transport.HttpTracker
import com.unitbean.analytics.transport.Tracker
import com.unitbean.analytics.transport.models.ActionRequest
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.coroutines.experimental.CoroutineContext

object UBAnalytics : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private const val DEVICE_ID = "device_id"
    private const val GOTO = "GoTo"

    private val deviceId: String by lazy { validateDeviceId() }
    private val sessionId: String by lazy { UUID.randomUUID().toString() }
    private val httpService: Tracker by lazy { HttpTracker(projectKey, sessionId) }

    private lateinit var preferences: SharedPreferences
    private lateinit var activityTracker: ActivityTracker

    private var projectKey: String? = null

    /**
     * Инициализация аналитики
     * @param context - контекст уровня [Application]
     * @param projectId - ключ проекта для ассоциации
     */
    fun init(context: Application, projectId: String) {
        if (projectId.trim().isEmpty()) {
            throw IllegalStateException("ProjectId cannot be empty")
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
                httpService.initSession(deviceId)
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
                val customParams = params.map { pair ->
                    pair.key to ActionRequest.CustomField(pair.value.toString(), "STRING")
                }.toMap()
                httpService.logEvent(tag, customParams)
            } catch (e: Exception) {
            }
        }
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Pair]
     */
    fun logEvent(tag: String, value: Pair<String, Any>) {
        launch {
            logEvent(tag, mapOf(value))
        }
    }

    /**
     * Логгирует кастомный ивент пользователя с аргументом [Bundle]
     */
    fun logEvent(tag: String, params: Bundle) {
        launch {
            logEvent(tag, HashMap<String, Any>().apply {
                for (key in params.keySet()) {
                    this[key] = params[key] ?: continue
                }
            })
        }
    }

    /**
     * Проверяет наличие [DEVICE_ID] в сохранённых настройках приложения
     * - присутствует: отдаем значение
     * - отсутствует: создаем новый идентификатор, сохраняем его и отдаем
     */
    private fun validateDeviceId(): String {
        return if (preferences.contains(DEVICE_ID)) {
            preferences.getString(DEVICE_ID, "") ?: ""
        } else {
            val newDeviceId = UUID.randomUUID().toString()
            preferences.edit().putString(DEVICE_ID, newDeviceId).apply()
            return newDeviceId
        }
    }
}