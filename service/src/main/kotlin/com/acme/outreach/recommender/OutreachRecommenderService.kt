package com.acme.outreach.recommender

import com.acme.outreach.models.OutreachChannel
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class OutreachRecommenderService {

    fun getOutreach(weather: String): OutreachChannel {
        logger.info {
            """
            Calculating outreach for weather
            """.trimIndent()
        }

        return OutreachChannel.UNKNOWN
    }
}
