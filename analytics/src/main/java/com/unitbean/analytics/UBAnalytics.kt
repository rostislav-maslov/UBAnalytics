package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.content.Context
import java.util.*

object UBAnalytics {

    private val sessionId by lazy { UUID.randomUUID().toString() }
    private val httpService by lazy { HttpService(sessionId) }

    private lateinit var activityService: ActivityService

    private var projectKey: String? = null

    /**
     * Инициализация аналитики
     * @param context - контекст
     * @param key - ключ проекта для ассоциации
     */
    fun init(context: Context, key: String) {
        if (context !is Application) {
            throw IllegalStateException("Call .init on Application context instance")
        }

        activityService = ActivityService(context, object : ActivityService.ActivityCallback {
            override fun onActivityStart(activity: Activity?) {
                httpService.screenOpen(activity?.localClassName ?: "Name activity not found")
            }
        })

        activityService.startTracking()

        projectKey = key
    }

    /**
     * Логгирует кастомный ивент пользователя
     * TODO сделать отсылку набора параметров
     */
    fun logEvent(tag: String, vararg params: Any) {
        httpService.logEvent(tag, params)
    }
}