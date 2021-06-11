package com.acme.outreach.controllers

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import com.acme.outreach.openweather.OpenWeatherService
import com.acme.outreach.recommender.OutreachRecommenderService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

        openWeatherService.getWeather(outreachRequest.city, outreachRequest.state, outreachRequest.date)
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

    @ExceptionHandler(WeatherDataNotFoundException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleWeatherDataNotFoundException() {
        logger.info {
            """
             Handling ${WeatherDataNotFoundException::class.simpleName}
             Return ${HttpStatus.BAD_REQUEST}
            """.trimIndent()
        }
    }
}
