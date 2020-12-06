package ktor.simple.rest.service.flat.utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import java.time.LocalDateTime

class FlatsGeneratorTimeBordersTest {

    private val generator = FlatsGenerator()

    /**
     * [FlatsGenerator.startDayTime] is 10:00, [FlatsGenerator. endDayTime] is 20:00.
     * It is default.
     */
    @TestFactory
    fun `first available slot time testing`() = listOf(
        LocalDateTime.parse("2020-01-01T08:30") to LocalDateTime.parse("2020-01-01T10:00"),
        LocalDateTime.parse("2020-01-01T10:00") to LocalDateTime.parse("2020-01-01T10:00"),
        LocalDateTime.parse("2020-01-01T15:00") to LocalDateTime.parse("2020-01-01T15:00"),
        LocalDateTime.parse("2020-01-01T10:07") to LocalDateTime.parse("2020-01-01T10:20"),
        LocalDateTime.parse("2020-01-01T19:59") to LocalDateTime.parse("2020-01-02T10:00"),
        LocalDateTime.parse("2020-01-01T21:00") to LocalDateTime.parse("2020-01-02T10:00")
    ).map { (startTime, expected) ->
        dynamicTest("start time: $startTime, expected first slot: $expected") {
            val dailySchedule = generator.generateFlat(startTime).schedules.first()
            dailySchedule.dateOfTheDay shouldBe expected.toLocalDate()
            dailySchedule.viewingSlots.first().startTime shouldBe expected.toLocalTime()
        }
    }
}