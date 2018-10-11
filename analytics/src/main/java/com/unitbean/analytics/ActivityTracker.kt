package com.unitbean.analytics

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityTracker(private val context: Application, private val callback: ActivityCallback) {

    private var isTracking = false
    private val listener = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
        }

        override fun onActivityResumed(activity: Activity?) {
        }

        override fun onActivityStarted(activity: Activity?) {
        }

        override fun onActivityDestroyed(activity: Activity?) {
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        }

        override fun onActivityStopped(activity: Activity?) {
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
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