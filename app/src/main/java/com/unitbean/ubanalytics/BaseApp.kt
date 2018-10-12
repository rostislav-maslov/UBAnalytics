package com.unitbean.ubanalytics

import android.app.Application
import com.unitbean.analytics.UBAnalytics

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()

        UBAnalytics.init(this, "qwerty")
    }
}