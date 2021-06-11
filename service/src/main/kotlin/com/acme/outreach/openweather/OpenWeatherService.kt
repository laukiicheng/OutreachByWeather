package com.acme.outreach.openweather

import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.util.Date

private val logger = KotlinLogging.logger {}

@Component
class OpenWeatherService {
    fun getWeather(city: String, state: String, date: Date) {
        logger.info {
            """
            Retrieving weather info from Open Weather API
            City $city
            State $state
            Date $date
            """.trimIndent()
        }
    }
}
