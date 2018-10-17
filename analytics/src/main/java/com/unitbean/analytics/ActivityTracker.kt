package com.unitbean.analytics

import android.support.v7.app.AppCompatActivity
import android.app.Application
import android.os.Bundle

@Suppress("UNUSED")
internal class ActivityTracker(private val context: Application, private val callback: ActivityCallback) {

    private var isTracking = false
    private val listener = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: AppCompatActivity?) {
        }

        override fun onActivityResumed(activity: AppCompatActivity?) {
        }

        override fun onActivityStarted(activity: AppCompatActivity?) {
        }

        override fun onActivityDestroyed(activity: AppCompatActivity?) {
        }

        override fun onActivitySaveInstanceState(activity: AppCompatActivity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: AppCompatActivity?) {
        }

        override fun onActivityCreated(activity: AppCompatActivity?, savedInstanceState: Bundle?) {
            callback.onActivityStart(activity)
        }
    }

    /**
     * Запускает отслеживание запусков активностей
     */
    fun startTracking() {
        context.registerActivityLifecycleCallbacks(listener)
        isTracking = true
    }

    /**
     * Останавливает отслеживание запусков активностей
     */
    fun stopTracking() {
        context.unregisterActivityLifecycleCallbacks(listener)
        isTracking = false
    }

    fun isTracking(): Boolean = isTracking
}

internal interface ActivityCallback {
    fun onActivityStart(activity: AppCompatActivity?)
}