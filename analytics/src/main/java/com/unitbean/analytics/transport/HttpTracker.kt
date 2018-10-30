package com.unitbean.analytics.transport

import android.os.Build
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.unitbean.analytics.UBAnalytics
import com.unitbean.analytics.transport.models.*
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.net.NetworkInterface
import java.util.concurrent.TimeUnit
import java.util.*

internal class HttpTracker(private val projectId: String) : Tracker {

    private var sessionId: String? = null

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (UBAnalytics.isDebuggable) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }))
            .addInterceptor { chain ->
                val original = chain.request()
                val method = original.method()

                val requestBuilder = original.newBuilder()
                    .method(method, original.body())

                if (!projectId.isEmpty())
                    requestBuilder.header("X-Auth-Token", projectId)

                if (!sessionId.isNullOrEmpty())
                    requestBuilder.header("X-Session-ID", sessionId!!)

                val request = requestBuilder.build()
                chain.proceed(request)
            }
            .build()
    }

    private val analytics by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    override suspend fun initSession(deviceId: String?, clientVersion: String) = analytics.initSession(InitRequest(deviceId, deviceData = InitRequest.DeviceData(getIPAddress(), "ANDROID", "${Build.MANUFACTURER} ${Build.MODEL}", clientVersion, "Android API ${Build.VERSION.SDK_INT}"))).await()

    override suspend fun setParams(tag: String, params: Map<String, Any>?) = analytics.setParams(tag, params).await()

    override suspend fun logEvent(type: String, sessionId: String, customFields: Map<String, ActionRequest.CustomField>?): BaseResponse<String> {
        if (this.sessionId.isNullOrEmpty()) {
            this.sessionId = sessionId
        }

        return analytics.logEvent(ActionRequest(type, customFields)).await()
    }

    override suspend fun userRegister(externalId: String, sessionId: String, customFields: Map<String, ActionRequest.CustomField>?): BaseResponse<String> {
        if (this.sessionId.isNullOrEmpty()) {
            this.sessionId = sessionId
        }

        return analytics.userRegister(UserRegisterRequest(externalId, customFields)).await()
    }

    override suspend fun utmSession(sessionId: String, source: String, medium: String, campaign: String, content: String, term: String): BaseResponse<String> {
        if (this.sessionId.isNullOrEmpty()) {
            this.sessionId = sessionId
        }

        return analytics.utmSession(UtmRequest(source, medium, campaign, content, term)).await()
    }

    private interface Api {
        @POST("$V1/session/register")
        fun initSession(@Body request: InitRequest): Deferred<BaseResponse<InitResponse>>

        @POST("$V1/session/custom-field")
        fun setParams(@Query("tag") tag: String,
                      @QueryMap map: Map<String, *>?): Deferred<BaseResponse<String>>

        @POST("$V1/action")
        fun logEvent(@Body request: ActionRequest): Deferred<BaseResponse<String>>

        @POST("$V1/user/register")
        fun userRegister(request: UserRegisterRequest): Deferred<BaseResponse<String>>

        @POST("$V1/session/utm")
        fun utmSession(request: UtmRequest): Deferred<BaseResponse<String>>
    }

    companion object {
        private const val BASE_URL = "https://analytics-api.unitbean.ru"
        private const val V1 = "/api/v1"

        /**
         * Получение локального IP-адреса устройства
         */
        internal fun getIPAddress(useIPv4: Boolean = true): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (networkInterface in interfaces) {
                    val networkAddresses = Collections.list(networkInterface.inetAddresses)
                    for (networkAddress in networkAddresses) {
                        if (!networkAddress.isLoopbackAddress) {
                            val address = networkAddress.hostAddress
                            val isIPv4 = address.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4)
                                    return address
                            } else {
                                if (!isIPv4) {
                                    val delimiter = address.indexOf('%') // drop ip6 zone suffix
                                    return if (delimiter < 0) address.toUpperCase() else address.substring(0, delimiter).toUpperCase()
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                if (UBAnalytics.isDebuggable) {
                    Log.e("UBAnalytics", e.message, e)
                }
            }
            return ""
        }
    }
}