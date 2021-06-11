package com.acme.outreach.models

import java.util.Date

data class OutreachRequest(
    val city: String,
    val state: String,
    val date: Date = Date()
)
