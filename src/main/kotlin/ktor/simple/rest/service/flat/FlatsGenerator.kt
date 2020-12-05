package ktor.simple.rest.service.flat

import ktor.simple.rest.service.flat.dtos.DailySchedule
import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import java.time.LocalDate
import java.time.LocalTime

/**
 * [upcomingDaysCount] is used for [generateFlat]. It affects count of days for which [Flat.schedules] will be generated.
 * e.g. if it's 7(default value) - schedules will be generated for current day + 6 upcoming days
 *
 * [viewingSlotTimeWindow] affects difference between [ViewingSlot.startTime] and [ViewingSlot.endTime] for each slot in [Flat.schedules]
 * Should be provided in minutes
 *
 * "Working" day window can be customised via [startDayTime] and [endDayTime]
 * slot can't be in time outside this window
 */
class FlatsGenerator(
    private val upcomingDaysCount: Long = 7,
    private val viewingSlotTimeWindow: Long = 20,

    private val startDayTime: LocalTime = LocalTime.of(10, 0),
    private val endDayTime: LocalTime = LocalTime.of(20, 0),
) {

    fun generateFlat(): Flat {
        val currentDay = LocalDate.now()

        return (0L until upcomingDaysCount)
            .map { dayNum ->
                DailySchedule(
                    dateOfTheDay = currentDay.plusDays(dayNum),
                    viewingSlots = generateSequence(startDayTime) { it.plusMinutes(viewingSlotTimeWindow) }
                        .takeWhile { it.isBefore(endDayTime) }
                        .map { ViewingSlot(it, it.plusMinutes(viewingSlotTimeWindow)) }
                        .toList()
                )
            }
            .let(::Flat)
    }

}