package com.acme.outreach.models

import java.time.LocalDateTime

data class OutreachResponse(
    val city: String,
    val stateCode: String,
    val countryCode: String,
    val outreachByDay: Map<LocalDateTime, OutreachChannel>
)
