package com.unitbean.analytics.transport.models

internal data class BaseResponse<T>(val result: T, val message: String?, val errors: List<String>?)