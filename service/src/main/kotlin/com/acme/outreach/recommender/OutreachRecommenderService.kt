package com.acme.outreach.recommender

import com.acme.outreach.models.GeneralWeather
import com.acme.outreach.models.OutreachChannel
import com.acme.outreach.models.WeatherData
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class OutreachRecommenderService {

    fun getOutreach(weather: WeatherData): OutreachChannel {
        logger.info {
            """
            Calculating outreach for weather
            """.trimIndent()
        }

        if (weather.general == GeneralWeather.SUNNY && weather.minimumTemp > 75) {
            return OutreachChannel.SMS
        }

        if (weather.minimumTemp >= 55 && weather.maximumTemp <= 75) {
            return OutreachChannel.EMAIL
        }

        if (weather.general == GeneralWeather.RAINY && weather.maximumTemp <= 55) {
            return OutreachChannel.IVR
        }

        return OutreachChannel.UNKNOWN
    }
}
