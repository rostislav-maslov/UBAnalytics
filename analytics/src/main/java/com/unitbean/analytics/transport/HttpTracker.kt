package com.unitbean.analytics.transport

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.unitbean.analytics.transport.models.ActionRequest
import com.unitbean.analytics.transport.models.BaseResponse
import com.unitbean.analytics.transport.models.InitRequest
import com.unitbean.analytics.transport.models.InitResponse
import kotlinx.coroutines.experimental.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

internal class HttpTracker(private val projectId: String?, private val sessionId: String) : Tracker {

    private val client by lazy { OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build() }

    private val analytics by lazy { Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(Api::class.java) }

    override suspend fun initSession(deviceId: String) = analytics.initSession(InitRequest(projectId, deviceId)).await()

    override suspend fun logEvent(type: String, customFields: Map<String, ActionRequest.CustomField>) = analytics.logEvent(ActionRequest(projectId, sessionId, type, customFields)).await()

    override suspend fun setParams(tag: String, params: Map<String, Any>?) = analytics.setParams(tag, params).await()

    private interface Api {
        @POST("$V1/session/register")
        fun initSession(@Body request: InitRequest): Deferred<BaseResponse<InitResponse>>

        @POST("$V1/action")
        fun logEvent(@Body request: ActionRequest): Deferred<BaseResponse<String>>

        @POST("$V1/session/custom-field")
        fun setParams(@Query("tag") tag: String,
                      @QueryMap map: Map<String, *>?): Deferred<BaseResponse<String>>
    }

    companion object {
        private const val BASE_URL = "https://analytics-api.unitbean.ru"
        private const val V1 = "/api/v1"
    }
}