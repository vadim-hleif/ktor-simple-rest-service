package ktor.simple.rest.service.flat.dtos

import ktor.simple.rest.service.flat.utils.IdGenerator
import ktor.simple.rest.service.landlord.dtos.Landlord

data class Flat(
    val id: Int = IdGenerator.genNextId(),
    val landlord: Landlord,
    val schedules: List<DailySchedule>,
)