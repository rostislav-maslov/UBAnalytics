package com.unitbean.analytics

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

class HttpService(private val sessionId: String) {

    private val client by lazy { OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build() }

    private val analytics by lazy { Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(Api::class.java) }

    fun logEvent(tag: String, vararg params: Any): Deferred<Any> = GlobalScope.async {

    }

    fun screenOpen(name: String): Deferred<Any> = GlobalScope.async {

    }

    private interface Api {
        @GET("/logEvent")
        fun logEvent(@Query("tag") tag: String): Deferred<Any>

        @GET("/screenOpen")
        fun screenOpen(@Query("name") name: String): Deferred<Any>
    }

    companion object {
        private const val BASE_URL = "https://ub-analytics.com"
    }
}