package com.acem.outreach.controllers

import com.acem.outreach.UtilityHelper
import com.acme.outreach.ServiceApplication
import com.acme.outreach.models.OutreachRequest
import com.acme.outreach.models.OutreachResponse
import io.kotest.matchers.shouldBe
import io.kotest.provided.BaseIntegrationTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [ServiceApplication::class])
// @TestPropertySource("${ResourceUtils.CLASSPATH_URL_PREFIX}${UtilityHelper.testPropertiesFilePath}")
class OutreachControllerIT(private val restTemplate: TestRestTemplate) : BaseIntegrationTest() {

    init {
        "POST should OK when city is Minneapolis and state is MN" {
            val outreachRequest = OutreachRequest(
                city = "Minneapolis",
                stateCode = "MN",
                countryCode = "US"
            )

            val result = restTemplate.postForEntity(
                "/outreach",
                UtilityHelper.createHttpEntity(outreachRequest),
                OutreachResponse::class.java
            )

            result.statusCode shouldBe HttpStatus.OK
            // TODO: Validate the response
        }
    }

    // TODO: Write failing test cases

    // TODO: Test ExceptionHandler in controller for WeatherDataNotFoundException
}
