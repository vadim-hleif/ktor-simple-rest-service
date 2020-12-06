package ktor.simple.rest.service.flat.dtos

import ktor.simple.rest.service.flat.utils.IdGenerator

data class Flat(
    val schedules: List<DailySchedule>,
    val id: Int = IdGenerator.genNextId(),
)