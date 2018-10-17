package com.unitbean.ubanalytics

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.unitbean.analytics.UBAnalytics

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    fun handleAction(v: View) {
        UBAnalytics.logEvent("Button", mapOf("Action" to "Custom", "AnotherCustomAction" to "AnotherCustomField", "ThridParam" to "ThirdValue", "NumberValue" to 31, "DoubleValue" to 4014.14))
    }
}