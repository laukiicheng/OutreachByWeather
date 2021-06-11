package io.kotest.provided

import io.kotest.core.listeners.TestListener
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object BaseIntegrationTestListener : TestListener {

    @ExperimentalTime
    private val monotonic = TimeSource.Monotonic

    @ExperimentalTime
    private lateinit var timeMark: TimeMark

    @ExperimentalTime
    override suspend fun beforeTest(testCase: TestCase) {
        timeMark = monotonic.markNow()
    }

    @ExperimentalTime
    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        println(
            """
            ------------------------------------------------
            TEST CASE INFORMATION
            Name: ${testCase.description.name}
            Origin: ${testCase.description.parents().first().name}
            Result: ${result.status}
            Run Time: ${timeMark.elapsedNow().toDouble(DurationUnit.MILLISECONDS)} MILLISECONDS
            ------------------------------------------------
            """.trimIndent()
        )
    }
}
