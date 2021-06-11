package io.kotest.provided

import com.revelhealth.channelservice.TestContainerUtils
import com.revelhealth.channelservice.UtilityHelper
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.spring.SpringAutowireConstructorExtension
import io.kotest.spring.SpringListener
import java.lang.Thread.sleep
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object ProjectConfig : AbstractProjectConfig() {

    @ExperimentalTime
    private val monotonic = TimeSource.Monotonic

    @ExperimentalTime
    private lateinit var timeMark: TimeMark

    override val parallelism: Int = 1

    override fun extensions() = listOf(SpringAutowireConstructorExtension)

    override fun listeners() = listOf(BaseIntegrationTestListener, SpringListener)

    override val globalAssertSoftly: Boolean = true

    @ExperimentalTime
    override fun beforeAll() {
        timeMark = monotonic.markNow()

        TestContainerUtils.startTestContainers()

        // TODO: MAS-518 Call health check to wait for service to start before executing integration tests. The service needs time to connect to it's dependencies (Mongo and Axon)
        sleep(5000)
    }

    @ExperimentalTime
    override fun afterAll() {
        TestContainerUtils.stopTestContainers()
        UtilityHelper.clearPropertiesFile()

        println(
            """
            ------------------------------------------------
            TOTAL TIME OF TEST SUITE RUN: ${timeMark.elapsedNow().inSeconds} SECONDS
            ------------------------------------------------
            """.trimIndent()
        )
    }
}
