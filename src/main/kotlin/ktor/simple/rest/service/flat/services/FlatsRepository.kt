package ktor.simple.rest.service.flat.services

import ktor.simple.rest.service.flat.utils.FlatsGenerator

class FlatsRepository {

    private val generator = FlatsGenerator()
    private val data = generator.generateFlat()

// TODO
//    fun reserve(tenant: Tenant, viewingSlot: ViewingSlot) {
//    }

}