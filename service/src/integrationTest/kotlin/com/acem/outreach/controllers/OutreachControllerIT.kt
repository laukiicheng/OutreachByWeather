package com.acem.outreach.controllers

import com.acem.outreach.UtilityHelper
import com.acme.outreach.ServiceApplication
import com.acme.outreach.models.OutreachRequest
import io.kotest.matchers.shouldBe
import io.kotest.provided.BaseIntegrationTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.util.ResourceUtils

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = [ServiceApplication::class])
@TestPropertySource("${ResourceUtils.CLASSPATH_URL_PREFIX}${UtilityHelper.testPropertiesFilePath}")
class OutreachControllerIT(private val restTemplate: TestRestTemplate) : BaseIntegrationTest() {

    init {
        "POST should return when" {
            val outreachRequest = OutreachRequest(
                city = "",
                stateCode = ""
            )

            val result = restTemplate.postForEntity(
                "/outreach",
                UtilityHelper.createHttpEntity(outreachRequest),
                String::class.java
            )

            result.statusCode shouldBe HttpStatus.CREATED
        }
    }
}
