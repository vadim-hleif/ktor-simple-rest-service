package ktor.simple.rest.service.flat.utils

import ktor.simple.rest.service.flat.dtos.DailySchedule
import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import ktor.simple.rest.service.landlord.dao.LandlordsRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.ceil

/**
 * [upcomingDaysCount] is used in [generateFlat] method. It affects count of days for each [Flat.schedules].
 * e.g. if it's 7(default value) - schedules will be generated for tomorrow day(if it's possible due to time windows) + 6 next days
 *
 * [viewingSlotTimeWindow] affects each slot in [Flat.schedules]
 * Should be provided in minutes
 *
 * "Working" day window can be customised via [startDayTime] and [endDayTime]
 * Viewing slot can't be in time outside this window
 */
class FlatsGenerator(
    private val upcomingDaysCount: Long = 7,
    private val viewingSlotTimeWindow: Long = 20,

    private val startDayTime: LocalTime = LocalTime.of(10, 0),
    private val endDayTime: LocalTime = LocalTime.of(20, 0),
) {

    /**
     * [startTime] default: current time + 24h. The first slot should be available in at least 24 hours.
     * Daily schedule is also limited between [startDayTime] and [endDayTime]
     *
     * examples:
     * [startTime] is 2020-01-01T10:00, [startDayTime] is 10:00, [endDayTime] is 20:00 - first slot at 2020-01-01T10:00
     * [startTime] is 2020-01-01T15:00, [startDayTime] is 10:00, [endDayTime] is 20:00 - first slot at 2020-01-01T15:00
     * [startTime] is 2020-01-01T21:00, [startDayTime] is 10:00, [endDayTime] is 20:00 - first slot at 2020-01-02T10:00
     * [startTime] is 2020-01-01T10:07, [startDayTime] is 10:00, [endDayTime] is 20:00 - first slot at 2020-01-01T10:20
     * [startTime] is 2020-01-01T10:30, [startDayTime] is 12:00, [endDayTime] is 20:00 - first slot at 2020-01-01T12:00
     * [startTime] is 2020-01-01T10:00, [startDayTime] is 10:00, [endDayTime] is 20:00 - first slot at 2020-01-01T19:59
     */
    fun generateFlat(startTime: LocalDateTime = LocalDateTime.now().plusHours(24)): Flat {
        val currentDayDate = startTime.toLocalDate()

        val firstDayDateTime = when {
            startTime.toLocalTime() < startDayTime -> currentDayDate.atTime(startDayTime)
            else -> {
                val adjustedTime = adjustTime(startTime.toLocalTime())

                if (adjustedTime < endDayTime)
                    startTime.toLocalDate().atTime(adjustedTime)
                else
                    currentDayDate.plusDays(1).atTime(startDayTime)
            }
        }

        val firstDaySchedule = generateScheduleForDay(firstDayDateTime.toLocalDate(), firstDayDateTime.toLocalTime())
        val nextDays = (1L until upcomingDaysCount)
            .map { dayNum -> generateScheduleForDay(currentDayDate.plusDays(dayNum), startDayTime) }

        return (sequenceOf(firstDaySchedule) + nextDays)
            .toList()
            .let { Flat(schedules = it, landlord = LandlordsRepository.findAll().random()) }
    }

    private fun generateScheduleForDay(dateOfTheDay: LocalDate, startDayTime: LocalTime) = DailySchedule(
        dateOfTheDay = dateOfTheDay,
        viewingSlots = generateSequence(startDayTime) { it.plusMinutes(viewingSlotTimeWindow) }
            .takeWhile { it.isBefore(endDayTime) }
            .map { ViewingSlot(it, it.plusMinutes(viewingSlotTimeWindow)) }
            .toList()
    )

    /**
     * Adjust start point to the first available slot, uses [viewingSlotTimeWindow] as step.
     * 10:03 -> 10:20
     * 10:19 -> 10:20
     * 19:50 -> 20:00
     * 19:59 -> 20:00
     * @see [generateFlat]
     */
    private fun adjustTime(startPoint: LocalTime): LocalTime {
        var startTimeMinutes = ceil(startPoint.minute / viewingSlotTimeWindow.toDouble()) * viewingSlotTimeWindow
        var startHour = startPoint.hour
        if (startTimeMinutes > 59) {
            startTimeMinutes = 0.0
            startHour += 1
        }

        return LocalTime.of(startHour, startTimeMinutes.toInt())
    }

}