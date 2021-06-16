package com.acme.outreach.controllers

import com.acme.outreach.models.GeneralWeather
import com.acme.outreach.models.OutreachChannel
import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import com.acme.outreach.models.WeatherData
import com.acme.outreach.models.WeatherForecast
import com.acme.outreach.openweather.OpenWeatherService
import com.acme.outreach.recommender.OutreachRecommenderService
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldNotBeInstanceOf
import io.kotest.provided.BaseStringSpec
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime
import org.springframework.http.HttpStatus

internal class OutreachControllerTest : BaseStringSpec() {

    private val getOutreachName = OutreachController::getOutreach.name

    private val currentDateTime = LocalDateTime.now()
    private val openWeatherService = mockk<OpenWeatherService> {
        every { getWeather(any(), any(), any()) } returns getWeatherForecast()
    }
    private val outreachRecommenderService = mockk<OutreachRecommenderService> {
        every { getOutreach(any()) } returns OutreachChannel.SMS
    }
    private val controller = OutreachController(openWeatherService, outreachRecommenderService)

    init {
        "$getOutreachName should return 5 ${OutreachResponse::class.simpleName}" {

            val outreachResponse = controller.getOutreach(
                OutreachRequest(
                    city = "Minneapolis",
                    stateCode = "MN",
                    countryCode = "US"
                )
            )

            with(outreachResponse) {
                statusCode shouldBe HttpStatus.OK
                body.shouldNotBeInstanceOf<OutreachResponse>()
                body shouldNotBe null
                body?.outreachByDay?.count() shouldBe 5
            }
        }

        // TODO: Write failing test cases
    }

    private fun getWeatherForecast(numberOfDaysToForecast: Int = 15) =
        WeatherForecast(
            days = (0 until numberOfDaysToForecast).map { getWeatherData(it.toLong()) }
        )

    private fun getWeatherData(day: Long) =
        WeatherData(
            dateTime = currentDateTime.plusDays(day),
            general = GeneralWeather.SUNNY,
            minimumTemp = 70.0,
            maximumTemp = 80.0
        )
}
