package com.acme.outreach.openweather.models

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenWeatherResponse(
    val list: List<DayPrediction>
)

data class DayPrediction(
    @JsonProperty("dt")
    val date: Int,

    @JsonProperty("dt_txt")
    val dateAsString: String,

    val main: Temperature,

    val weather: List<AllDayForecast>
)

data class Temperature(
    val temp: Double,

    @JsonProperty("feels_like")
    val feelsLike: Double,

    @JsonProperty("temp_min")
    val minimumTemperature: Double,

    @JsonProperty("temp_max")
    val maximumTemperature: Double
)

data class AllDayForecast(
    val main: String
)
