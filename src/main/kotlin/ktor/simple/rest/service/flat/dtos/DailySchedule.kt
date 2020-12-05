package ktor.simple.rest.service.flat.dtos

import java.time.LocalDate

data class DailySchedule(
     val dateOfTheDay: LocalDate,
     val viewingSlots: List<ViewingSlot>,
)