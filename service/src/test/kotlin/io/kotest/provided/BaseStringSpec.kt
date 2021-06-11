package io.kotest.provided

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.StringSpec

open class BaseStringSpec : StringSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest
}
