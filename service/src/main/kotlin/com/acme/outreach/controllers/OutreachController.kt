package com.acme.outreach.controllers

import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import com.acme.outreach.openweather.OpenWeatherService
import com.acme.outreach.recommender.OutreachRecommenderService
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/outreach")
class OutreachController(
    private val openWeatherService: OpenWeatherService,
    private val outreachRecommenderService: OutreachRecommenderService
) {

    @PostMapping
    fun getOutreach(@RequestBody outreachRequest: OutreachRequest): ResponseEntity<OutreachResponse> {
        logger.info {
            """
            Received ${OutreachRequest::class.simpleName}
            City ${outreachRequest.city}
            State ${outreachRequest.state}
            Date ${outreachRequest.date}
            """.trimIndent()
        }

        val weather = openWeatherService.getWeather(outreachRequest.city, outreachRequest.state, outreachRequest.date)
        val outreachRecommendation = outreachRecommenderService.getOutreach("")

        return ResponseEntity.ok(
            outreachRequest.let {
                OutreachResponse(
                    city = it.city,
                    state = it.state,
                    date = it.date,
                    outreachRecommendation
                )
            }
        )
    }
}
