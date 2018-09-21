package com.unitbean.analytics

import android.app.Activity

internal interface ActivityCallback {
    fun onActivityStart(activity: Activity?)
    fun onActivityDestroyed(activity: Activity?)
}