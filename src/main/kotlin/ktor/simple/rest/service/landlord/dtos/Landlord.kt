package ktor.simple.rest.service.landlord.dtos

import ktor.simple.rest.service.flat.utils.IdGenerator

data class Landlord(
    val id: Int = IdGenerator.genNextId(),
)