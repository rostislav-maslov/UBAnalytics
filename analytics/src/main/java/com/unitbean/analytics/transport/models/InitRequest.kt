package com.unitbean.analytics.transport.models

internal data class InitRequest(
    val token: String,
    val deviceId: String?,
    val type: String = "ANDROID",
    val deviceData: DeviceData? = null
) {
    internal data class DeviceData(
        val ip: String,
        val deviceType: String,
        val device: String,
        val clientVersion: String,
        val system: String
    )
}