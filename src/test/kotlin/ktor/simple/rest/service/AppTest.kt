package ktor.simple.rest.service

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class AppTest {

    @Test
    fun `greeting field -- hello world string`() {
        val classUnderTest = App()

        classUnderTest.greeting shouldBe "Hello world."
    }

}
