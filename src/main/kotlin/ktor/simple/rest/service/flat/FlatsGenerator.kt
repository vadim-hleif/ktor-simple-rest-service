package ktor.simple.rest.service.flat

import ktor.simple.rest.service.flat.dtos.DailySchedule
import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

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
        val schedules = LinkedList<DailySchedule>()

        // generate for one week
        for (dayNum in 0L until upcomingDaysCount) {
            val slots = LinkedList<ViewingSlot>()

            var slotStartTime = startDayTime
            var slotEndTime = startDayTime.plusMinutes(viewingSlotTimeWindow)

            while (!slotEndTime.isAfter(endDayTime)) {
                slots.add(ViewingSlot(slotStartTime, slotEndTime))

                slotStartTime = slotEndTime
                slotEndTime = slotStartTime.plusMinutes(20)
            }

            val currentDay = LocalDate.now().plusDays(dayNum)
            schedules.add(DailySchedule(currentDay, slots))
        }


        return Flat(schedules)
    }

}