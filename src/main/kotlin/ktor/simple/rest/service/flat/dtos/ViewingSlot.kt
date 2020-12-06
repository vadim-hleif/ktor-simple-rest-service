package ktor.simple.rest.service.flat.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import ktor.simple.rest.service.flat.utils.IdGenerator
import ktor.simple.rest.service.tenant.dtos.Tenant
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicReference

data class ViewingSlot(
    @JsonFormat(pattern = "HH:mm") val startTime: LocalTime,
    @JsonFormat(pattern = "HH:mm") val endTime: LocalTime,
    val id: Int = IdGenerator.genNextId(),
    var bookedBy: AtomicReference<Tenant?> = AtomicReference(null),
    var state: SlotState? = null,
)