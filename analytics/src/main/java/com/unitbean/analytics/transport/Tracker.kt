package com.unitbean.analytics.transport

import com.unitbean.analytics.transport.models.ActionRequest
import com.unitbean.analytics.transport.models.BaseResponse
import com.unitbean.analytics.transport.models.InitResponse

internal interface Tracker {
    suspend fun initSession(deviceId: String?, clientVersion: String): BaseResponse<InitResponse>
    suspend fun setParams(tag: String, params: Map<String, Any>? = null): BaseResponse<String>
    suspend fun logEvent(type: String, sessionId: String, customFields: Map<String, ActionRequest.CustomField>? = null): BaseResponse<String>
    suspend fun userRegister(externalId: String, sessionId: String, customFields: Map<String, ActionRequest.CustomField>? = null): BaseResponse<String>
    suspend fun utmSession(sessionId: String, source: String, medium: String, campaign: String, content: String, term: String): BaseResponse<String>
}

internal enum class TrackerTypes {
    STRING,
    INT,
    DOUBLE,
    LONG,
    FLOAT,
    ARRAY;

    companion object {

        /**
         * Конвертация любого переданного объекта в тип, известный системе аналитики
         * Умеет работать со [String], [Int], [Double], [Long], [Float], [Array]
         */
        fun getType(someObject: Any): TrackerTypes {
            return when (someObject) {
                is String -> STRING
                is Int -> INT
                is Double -> DOUBLE
                is Array<*> -> ARRAY
                is Long -> LONG
                is Float -> FLOAT
                else -> throw IllegalArgumentException("Unsupported type: ${someObject.javaClass}")
            }
        }
    }
}

internal enum class TypeTypes {
    GO_TO,              // для трека перехода на новый экран
    ERROR,              // для трека ошибки (?)
    WARNING,            // для трека опасного состояния (?)
    CRASH               // для трека падения приложения (?)
}