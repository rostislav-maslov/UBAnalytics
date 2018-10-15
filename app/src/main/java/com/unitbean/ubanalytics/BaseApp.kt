package com.unitbean.ubanalytics

import android.app.Application
import android.util.Log
import com.unitbean.analytics.UBAnalytics
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import java.lang.Exception

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch {
            try {
                UBAnalytics.apply {
                    isDebuggable = true
                }.init(this@BaseApp, "5bc44ef959201a03bf7c8a685bc44f0159201a03bf7c8a695bc44f0159201a03bf7c8a6a", BuildConfig.VERSION_NAME)
            } catch (e: Exception) {
                Log.e("Application", e.message, e)
            }
        }
    }
}