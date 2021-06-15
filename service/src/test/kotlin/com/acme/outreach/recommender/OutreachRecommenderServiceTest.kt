package com.acme.outreach.recommender

import com.acme.outreach.models.GeneralWeather
import com.acme.outreach.models.OutreachChannel
import com.acme.outreach.models.WeatherData
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.provided.BaseStringSpec
import java.time.LocalDate

internal class OutreachRecommenderServiceTest : BaseStringSpec() {
    private val getOutreachName = OutreachRecommenderService::getOutreach.name

    private val outreachService = OutreachRecommenderService()

    init {

        "$getOutreachName should return ${OutreachChannel.SMS} when weather is ${GeneralWeather.SUNNY} and minimum temp is >= 75" {
            forAll(
                row(getWeather(GeneralWeather.SUNNY, 75.0, 80.0)),
                row(getWeather(GeneralWeather.SUNNY, 80.0, 90.0)),
                row(getWeather(GeneralWeather.SUNNY, 90.0, 91.0))
            ) { weatherData: WeatherData ->
                outreachService.getOutreach(weatherData) shouldBe OutreachChannel.SMS
            }
        }

        "$getOutreachName should return ${OutreachChannel.EMAIL} when minimum temp is >= 55 and maximum temp <= 75" {
            forAll(
                row(getWeather(GeneralWeather.CLOUDY, 55.0, 75.0)),
                row(getWeather(GeneralWeather.RAINY, 60.0, 70.0)),
                row(getWeather(GeneralWeather.CLOUDY, 70.0, 70.0))
            ) { weatherData: WeatherData ->
                outreachService.getOutreach(weatherData) shouldBe OutreachChannel.EMAIL
            }
        }

        "$getOutreachName should return ${OutreachChannel.IVR} when weather is ${GeneralWeather.RAINY} and maximum temp <= 55" {
            forAll(
                row(getWeather(GeneralWeather.RAINY, 40.0, 55.0)),
                row(getWeather(GeneralWeather.RAINY, 50.0, 51.0)),
                row(getWeather(GeneralWeather.RAINY, 53.0, 55.0))
            ) { weatherData: WeatherData ->
                outreachService.getOutreach(weatherData) shouldBe OutreachChannel.IVR
            }
        }

        "$getOutreachName should return ${OutreachChannel.UNKNOWN}" {
            forAll(
                row(getWeather(GeneralWeather.CLOUDY, 40.0, 55.0)),
                row(getWeather(GeneralWeather.SUNNY, 50.0, 51.0)),
                row(getWeather(GeneralWeather.RAINY, 53.0, 90.0))
            ) { weatherData: WeatherData ->
                outreachService.getOutreach(weatherData) shouldBe OutreachChannel.UNKNOWN
            }
        }

        "$getOutreachName should throw ${IllegalArgumentException::class.simpleName} when minimum temp > maximum temp" {
            forAll(
                row(getWeather(GeneralWeather.SUNNY, 50.0, 30.0)),
                row(getWeather(GeneralWeather.CLOUDY, 90.0, 80.0)),
                row(getWeather(GeneralWeather.RAINY, 100.0, 90.0))
            ) { weatherData: WeatherData ->
                shouldThrowExactly<IllegalArgumentException> {
                    outreachService.getOutreach(weatherData)
                }
            }
        }
    }

    private fun getWeather(
        generalWeather: GeneralWeather = GeneralWeather.SUNNY,
        minimumTemperature: Double = 0.0,
        maximumTemperature: Double = 0.0
    ): WeatherData =
        WeatherData(
            date = LocalDate.now(),
            general = generalWeather,
            minimumTemp = minimumTemperature,
            maximumTemp = maximumTemperature
        )
}
