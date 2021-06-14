package com.acme.outreach.models

import java.time.LocalDate

data class WeatherForecast(
    val days: List<WeatherData>
)

data class WeatherData(
    val date: LocalDate,
    val general: GeneralWeather,
    val minimumTemp: Double,
    val maximumTemp: Double
)

enum class GeneralWeather {
    SUNNY,
    CLOUDY,
    RAINY
}

fun getGeneralWeather(value: String): GeneralWeather =
    when (value) {
        "Clear" -> GeneralWeather.SUNNY
        "Clouds" -> GeneralWeather.CLOUDY
        "Rain" -> GeneralWeather.RAINY
        else -> throw IllegalArgumentException("Unable to get ${GeneralWeather::class.simpleName} for $value")
    }
