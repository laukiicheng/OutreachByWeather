package io.kotest.provided

import io.kotest.core.config.AbstractProjectConfig
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object ProjectConfig : AbstractProjectConfig() {

    @ExperimentalTime
    private val monotonic = TimeSource.Monotonic

    @ExperimentalTime
    private lateinit var timeMark: TimeMark

    override val parallelism: Int = 1

    override fun listeners() = listOf(BaseTestListener)

    override val globalAssertSoftly: Boolean = true

    @ExperimentalTime
    override fun beforeAll() {
        timeMark = monotonic.markNow()
    }

    @ExperimentalTime
    override fun afterAll() {
        println(
            """
            ------------------------------------------------
            TOTAL TIME OF TEST SUITE RUN: ${timeMark.elapsedNow().toDouble(DurationUnit.SECONDS)} SECONDS
            ------------------------------------------------
            """.trimIndent()
        )
    }
}
