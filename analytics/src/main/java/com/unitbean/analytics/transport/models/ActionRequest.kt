package com.unitbean.analytics.transport.models

import com.unitbean.analytics.transport.TrackerTypes

internal data class ActionRequest(val type: String, val customFields: Map<String, CustomField>?) {
    internal data class CustomField(val value: Any, val type: TrackerTypes)
}