package io.kotest.provided

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec

open class BaseIntegrationTest : StringSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
}
