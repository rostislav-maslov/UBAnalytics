package com.unitbean.analytics.transport

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import java.util.concurrent.TimeUnit

internal class HttpService(private val sessionId: String) : Transport {

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

    override fun initSession(projectId: String): Deferred<Any> = analytics.initSession(projectId, sessionId)

    override fun logEvent(tag: String, params: Map<String, Any>?): Deferred<Any> = analytics.logEvent(tag, params)

    override fun screenOpen(name: String, params: Map<String, Any>?): Deferred<Any> = analytics.screenOpen(name, params)

    private interface Api {
        @GET("/initSession")
        fun initSession(@Query("projectId") projectId: String, @Query("sessionId") sessionId: String): Deferred<Any>

        @GET("/logEvent")
        fun logEvent(@Query("tag") tag: String, @QueryMap map: Map<String, *>?): Deferred<Any>

        @GET("/screenOpen")
        fun screenOpen(@Query("name") name: String, @QueryMap map: Map<String, *>?): Deferred<Any>
    }

    companion object {
        private const val BASE_URL = "https://ub-analytics.com"
    }
}