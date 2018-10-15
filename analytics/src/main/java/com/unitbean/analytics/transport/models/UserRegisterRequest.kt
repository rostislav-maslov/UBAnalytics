package com.unitbean.analytics.transport.models

internal data class UserRegisterRequest(val externalId: String, val customFields: Map<String, ActionRequest.CustomField>?)