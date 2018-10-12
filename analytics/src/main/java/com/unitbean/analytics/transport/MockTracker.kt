package com.unitbean.analytics.transport

import com.unitbean.analytics.transport.models.BaseResponse
import com.unitbean.analytics.transport.models.InitResponse

@Suppress("UNUSED")
internal class MockTracker(private val sessionId: String) : Tracker {

    override suspend fun initSession(deviceId: String?): BaseResponse<InitResponse> = BaseResponse(InitResponse("SessionId", "DeviceId"), null, null)

    override suspend fun logEvent(type: String, sessionId: String, customFields: Map<String, Any>?): BaseResponse<String> = BaseResponse("Ok", null, null)

    override suspend fun setParams(tag: String, params: Map<String, Any>?): BaseResponse<String> = BaseResponse("Ok", null, null)
}