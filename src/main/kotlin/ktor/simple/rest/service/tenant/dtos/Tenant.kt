package ktor.simple.rest.service.tenant.dtos

import ktor.simple.rest.service.flat.utils.IdGenerator

data class Tenant(
    val id: Int = IdGenerator.genNextId(),
)