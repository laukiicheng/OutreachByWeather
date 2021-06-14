package com.acme.outreach.models

import java.time.LocalDate

data class OutreachResponse(
    val city: String,
    val stateCode: String,
    val countryCode: String,
    val outreachByDay: Map<LocalDate, OutreachChannel>
)
