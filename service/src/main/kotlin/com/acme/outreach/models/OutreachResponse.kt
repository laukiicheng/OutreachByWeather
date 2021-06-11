package com.acme.outreach.models

import java.util.Date

data class OutreachResponse(
    val city: String,
    val state: String,
    val date: Date,
    val channel: OutreachChannel
)
