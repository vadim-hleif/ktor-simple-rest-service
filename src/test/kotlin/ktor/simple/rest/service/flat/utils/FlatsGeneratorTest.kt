package ktor.simple.rest.service.flat.utils

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.LocalTime

class FlatsGeneratorTest {

    private val generator = FlatsGenerator()

    @Test
    fun `produced flat -- has slots for the 7 upcoming days`() {
        val daysSchedules = generator.generateFlat().schedules
        daysSchedules.shouldNotBeEmpty()

        val firstDayTime = daysSchedules.first().dateOfTheDay.atStartOfDay()
        val latDayTime = daysSchedules.last().dateOfTheDay.atStartOfDay()

        // current + 6 upcoming
        Duration.between(firstDayTime, latDayTime) shouldBe Duration.ofDays(6)
    }

    @Test
    fun `produced flat -- each viewing slot has 20 minute interval`() {
        val daysSchedules = generator.generateFlat().schedules
        daysSchedules.shouldNotBeEmpty()

        for (daySchedule in daysSchedules) {

            daySchedule.viewingSlots.shouldNotBeEmpty()
            for ((startTime, endTime) in daySchedule.viewingSlots) {
                Duration.between(startTime, endTime) shouldBe Duration.ofMinutes(20)
            }
        }
    }

    @Test
    fun `produced flat -- slots are available from 10 am to 20 pm`() {
        val daysSchedules = generator.generateFlat().schedules
        daysSchedules.shouldNotBeEmpty()

        for ((_, viewingSlots) in daysSchedules) {
            viewingSlots.shouldNotBeEmpty()
            viewingSlots.first().startTime shouldBe LocalTime.of(10, 0)
            viewingSlots.last().endTime shouldBe LocalTime.of(20, 0)
        }
    }

}