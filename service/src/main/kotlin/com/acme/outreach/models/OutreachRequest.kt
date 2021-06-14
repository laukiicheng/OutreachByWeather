package com.acme.outreach.models

import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class OutreachRequest(
    @field:NotNull
    @field:NotEmpty
    @field:Valid
    val city: String,

    @field:NotNull
    @field:NotEmpty
    @field:Valid
    val stateCode: String,

    @field:NotNull
    @field:NotEmpty
    @field:Valid
    val countryCode: String
)
