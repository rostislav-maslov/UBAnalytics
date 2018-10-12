package com.unitbean.analytics.transport

import com.unitbean.analytics.transport.models.ActionRequest
import com.unitbean.analytics.transport.models.BaseResponse
import com.unitbean.analytics.transport.models.InitResponse

internal class MockTracker(private val sessionId: String) : Tracker {

    override suspend fun initSession(deviceId: String): BaseResponse<InitResponse> = BaseResponse(InitResponse("SessionId", "DeviceId"), null, null)

    override suspend fun logEvent(type: String, customFields: Map<String, ActionRequest.CustomField>): BaseResponse<String> = BaseResponse("Ok", null, null)

    override suspend fun setParams(tag: String, params: Map<String, Any>?): BaseResponse<String> = BaseResponse("Ok", null, null)
}