package com.unitbean.analytics.transport

import com.unitbean.analytics.transport.models.BaseResponse
import com.unitbean.analytics.transport.models.InitResponse

internal interface Tracker {
    suspend fun initSession(deviceId: String?): BaseResponse<InitResponse>
    suspend fun setParams(tag: String, params: Map<String, Any>? = null): BaseResponse<String>
    suspend fun logEvent(type: String, sessionId: String, customFields: Map<String, Any>? = null): BaseResponse<String>
}