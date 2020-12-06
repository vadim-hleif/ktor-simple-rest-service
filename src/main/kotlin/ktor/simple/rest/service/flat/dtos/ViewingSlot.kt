package ktor.simple.rest.service.flat.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalTime

data class ViewingSlot(
    @JsonFormat(pattern = "HH:mm") val startTime: LocalTime,
    @JsonFormat(pattern = "HH:mm") val endTime: LocalTime,
)