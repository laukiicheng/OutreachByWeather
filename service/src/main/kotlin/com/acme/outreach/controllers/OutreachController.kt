package com.acme.outreach.controllers

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import com.acme.outreach.openweather.OpenWeatherService
import com.acme.outreach.recommender.OutreachRecommenderService
import javax.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/outreach")
class OutreachController(
    private val openWeatherService: OpenWeatherService,
    private val outreachRecommenderService: OutreachRecommenderService
) {

    @PostMapping
    fun getOutreach(
        @Valid @RequestBody
        outreachRequest: OutreachRequest
    ): ResponseEntity<OutreachResponse> {
        logger.info {
            """
            Received ${OutreachRequest::class.simpleName}
            City ${outreachRequest.city}
            State ${outreachRequest.stateCode}
            """.trimIndent()
        }

        // openWeatherService.getWeather(outreachRequest.city, outreachRequest.stateCode)
        val outreachRecommendation = outreachRecommenderService.getOutreach("")

        return ResponseEntity.ok(
            outreachRequest.let {
                OutreachResponse(
                    city = it.city,
                    stateCode = it.stateCode,
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
