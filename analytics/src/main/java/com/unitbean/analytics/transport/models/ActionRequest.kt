package com.unitbean.analytics.transport.models

internal data class ActionRequest(val token: String, val sessionId: String, val type: String, val customFields: Map<String, Any>?)