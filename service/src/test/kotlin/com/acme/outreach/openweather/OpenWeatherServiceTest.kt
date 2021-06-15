package com.acme.outreach.openweather

import com.acme.outreach.exceptions.WeatherDataNotFoundException
import com.acme.outreach.models.GeneralWeather
import com.acme.outreach.models.WeatherForecast
import com.acme.outreach.openweather.models.AllDayForecast
import com.acme.outreach.openweather.models.DayPrediction
import com.acme.outreach.openweather.models.OpenWeatherResponse
import com.acme.outreach.openweather.models.Temperature
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import io.kotest.provided.BaseStringSpec
import io.mockk.every
import io.mockk.mockk
import java.net.URI
import java.time.LocalDate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

internal class OpenWeatherServiceTest : BaseStringSpec() {

    private val getWeatherName = OpenWeatherService::getWeather.name
    private val weatherForecastName = WeatherForecast::class.simpleName
    private val weatherDataNotFoundExceptionName = com.acme.outreach.exceptions.WeatherDataNotFoundException::class.simpleName

    private val restTemplate = mockk<RestTemplate>()
    private val openWeatherService = OpenWeatherService(restTemplate).apply {
        openWeatherApiKey = ""
        openWeatherApiUrl = "http://api.openweathermap.org/data/2.5/forecast"
    }

    init {
        "$getWeatherName should return $weatherForecastName" {
            val response = getExampleOpenWeatherResponse()
            val mockResponse = mockk<ResponseEntity<OpenWeatherResponse>> {
                every { statusCode } returns HttpStatus.OK
                every { body } returns response
            }

            every {
                restTemplate.getForEntity(any<URI>(), OpenWeatherResponse::class.java)
            } returns mockResponse

            val weatherForecast = openWeatherService.getWeather("Minneapolis", "MN", "US")

            weatherForecast.days.count() shouldBe 1
            val dayOne = weatherForecast.days.first()
            with(dayOne) {
                dateTime shouldBe LocalDate.parse("2021-06-14")
                general shouldBe GeneralWeather.CLOUDY
                minimumTemp shouldBe 70
                maximumTemp shouldBe 80
            }
        }

        "$getWeatherName should throw $weatherDataNotFoundExceptionName when status code is not OK" {
            val mockResponse = mockk<ResponseEntity<OpenWeatherResponse>> {
                every { statusCode } returns HttpStatus.BAD_REQUEST
                every { body } returns null
            }

            every {
                restTemplate.getForEntity(any<URI>(), OpenWeatherResponse::class.java)
            } returns mockResponse

            shouldThrowExactly<WeatherDataNotFoundException> {
                openWeatherService.getWeather("Minneapolis", "MN", "US")
            }
        }

        "$getWeatherName should throw $weatherDataNotFoundExceptionName when response body is null" {
            val mockResponse = mockk<ResponseEntity<OpenWeatherResponse>> {
                every { statusCode } returns HttpStatus.OK
                every { body } returns null
            }

            every {
                restTemplate.getForEntity(any<URI>(), OpenWeatherResponse::class.java)
            } returns mockResponse

            shouldThrowExactly<WeatherDataNotFoundException> {
                openWeatherService.getWeather("Minneapolis", "MN", "US")
            }
        }
    }

    private fun getExampleOpenWeatherResponse() =
        OpenWeatherResponse(
            list = listOf(
                DayPrediction(
                    dateAsString = "2021-06-14 21:00:00",
                    main = Temperature(
                        temp = 75.0,
                        feelsLike = 75.0,
                        minimumTemperature = 70.0,
                        maximumTemperature = 80.0
                    ),
                    weather = listOf(
                        AllDayForecast(
                            main = "Clouds"
                        )
                    )
                )
            )
        )
}
