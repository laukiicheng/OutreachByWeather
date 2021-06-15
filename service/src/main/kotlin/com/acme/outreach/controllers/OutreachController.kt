package com.acme.outreach.controllers

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import com.acme.outreach.openweather.OpenWeatherService
import com.acme.outreach.recommender.OutreachRecommenderService
import java.time.LocalTime
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

        val weatherForecast = openWeatherService.getWeather(outreachRequest.city, outreachRequest.stateCode, outreachRequest.countryCode)

        // The weather forecast for each day by every 3 hours. Let's assume we will take the forecast for noon.
        val weatherForecastSorted = weatherForecast.days.filter { it.dateTime.toLocalTime() == LocalTime.NOON }.sortedBy { it.dateTime }
        val weatherForecastFiveDays = weatherForecastSorted.take(5)

        val outreachRecommendation = OutreachResponse(
            city = outreachRequest.city,
            stateCode = outreachRequest.stateCode,
            countryCode = outreachRequest.countryCode,
            outreachByDay = weatherForecastFiveDays.associate { it.dateTime to outreachRecommenderService.getOutreach(it) }
        )

        return ResponseEntity.ok(outreachRecommendation)
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
