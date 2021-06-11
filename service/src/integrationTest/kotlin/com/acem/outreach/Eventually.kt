package com.acem.outreach

import io.kotest.assertions.timing.eventually
import org.opentest4j.AssertionFailedError
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

/**
 * Default eventually to use for assertions
 * TODO: MAS-517 This is a work around until the bug in Kotlin Test eventually gets fixed
 * https://github.com/kotlintest/kotlintest/issues/962w
 * @param waitForAxon Sleep 400 milliseconds to allow for eventual consistency to succeed/complete
 * @param f Function to be executed until either it returns or the time has exceeded the timeout
 */
@ExperimentalTime
suspend inline fun <T> eventually(waitForAxon: Boolean = false, crossinline f: () -> T): T {
    return eventually(Eventually.MAX_WAIT_MS.milliseconds, AssertionFailedError::class) {
        if (waitForAxon) {
            Thread.sleep(Eventually.WAIT_FOR_AXON_MS)
        }
        f()
    }
}

class Eventually {
    companion object {
        const val MAX_WAIT_MS = 2500
        const val WAIT_FOR_AXON_MS: Long = 400
    }
}
