package com.acme.outreach.openweather

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
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

    fun getWeather(city: String, state: String) {
        logger.info {
            """
            Retrieving weather info from Open Weather API
            City $city
            State $state
            """.trimIndent()
        }

        val uri = UriComponentsBuilder
            .fromUriString(openWeatherApiUrl)
            .queryParam(QUERY_PARAM_NAME, URLEncoder.encode("$city,$state", StandardCharsets.UTF_8.toString()))
            .queryParam(API_KEY_PARAM_NAME, URLEncoder.encode(openWeatherApiKey, StandardCharsets.UTF_8.toString()))
            .build()
            .toUriString()

        val weatherResponse = restTemplate.getForEntity(
            URI(uri),
            String::class.java
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
    }

    companion object {
        const val QUERY_PARAM_NAME = "q"
        const val API_KEY_PARAM_NAME = "appid"
    }
}
