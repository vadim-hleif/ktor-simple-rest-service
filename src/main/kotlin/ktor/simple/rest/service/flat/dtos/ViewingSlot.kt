package ktor.simple.rest.service.flat.dtos

import java.time.LocalTime

data class ViewingSlot(
    val startTime: LocalTime,
    val endTime: LocalTime,
)