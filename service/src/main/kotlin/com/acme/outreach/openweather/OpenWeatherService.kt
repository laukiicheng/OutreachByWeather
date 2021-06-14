package com.acme.outreach.openweather

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import com.acme.outreach.models.WeatherData
import com.acme.outreach.models.WeatherForecast
import com.acme.outreach.models.getGeneralWeather
import com.acme.outreach.openweather.models.OpenWeatherResponse
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

private val logger = KotlinLogging.logger {}

@Component
class OpenWeatherService(private val restTemplate: RestTemplate) {

    @Value("\${openWeather.apiKey}")
    lateinit var openWeatherApiKey: String

    @Value("\${openWeather.Url}")
    lateinit var openWeatherApiUrl: String

    fun getWeather(city: String, state: String, countryCode: String): WeatherForecast {
        logger.info {
            """
            Retrieving weather info from Open Weather API
            City $city
            State $state
            """.trimIndent()
        }

        val uri = UriComponentsBuilder
            .fromUriString(openWeatherApiUrl)
            .queryParam(QUERY_PARAM_NAME, URLEncoder.encode("$city,$state,$countryCode", StandardCharsets.UTF_8.toString()))
            .queryParam(UNITS_PARAM_NAME, URLEncoder.encode(UNITS_PARAM_IMPERIAL, StandardCharsets.UTF_8.toString()))
            .queryParam(API_KEY_PARAM_NAME, URLEncoder.encode(openWeatherApiKey, StandardCharsets.UTF_8.toString()))
            .build()
            .toUriString()

        val weatherResponse = restTemplate.getForEntity(
            URI(uri),
            OpenWeatherResponse::class.java
        )

        if (weatherResponse.statusCode != HttpStatus.OK) {
            // TODO: Check if invalid city or country code
            throw WeatherDataNotFoundException(
                """
                Open Weather API weather data not found
                Http status code ${weatherResponse.statusCode}
                Body ${weatherResponse.body}
                City $city
                State $state
                """.trimIndent()
            )
        }

        if (weatherResponse.body == null) {
            throw WeatherDataNotFoundException(
                """
                Open Weather API weather data not found
                Response body is null
                City $city
                State $state
                """.trimIndent()
            )
        }

        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val daysOfForecast = weatherResponse.body?.list?.map {
            WeatherData(
                date = LocalDate.parse(it.dateAsString, dateTimeFormatter),
                general = getGeneralWeather(it.weather.first().main),
                minimumTemp = it.main.minimumTemperature,
                maximumTemp = it.main.maximumTemperature
            )
        } ?: throw IllegalStateException("Unable to create weather data")

        return WeatherForecast(days = daysOfForecast)
    }

    companion object {
        const val QUERY_PARAM_NAME = "q"
        const val UNITS_PARAM_NAME = "units"
        const val UNITS_PARAM_IMPERIAL = "imperial"
        const val API_KEY_PARAM_NAME = "appid"
    }
}
