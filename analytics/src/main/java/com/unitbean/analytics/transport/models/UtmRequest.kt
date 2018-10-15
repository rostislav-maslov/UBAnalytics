package com.unitbean.analytics.transport.models

internal data class UtmRequest(val source: String,
                               val medium: String,
                               val campaign: String,
                               val content: String,
                               val term: String)