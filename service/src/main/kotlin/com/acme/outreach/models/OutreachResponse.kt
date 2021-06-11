package com.acme.outreach.models

data class OutreachResponse(
    val city: String,
    val stateCode: String,
    val channel: OutreachChannel
)
