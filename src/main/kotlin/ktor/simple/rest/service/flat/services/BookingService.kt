package ktor.simple.rest.service.flat.services

import ktor.simple.rest.service.exceptions.EntityNotFoundException
import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.tenant.dao.TenantsRepository

object BookingService {

    fun reserveSlot(tenantId: Int, flatId: Int, slotId: Int) {
        val flat = FlatsRepository.findOne(flatId) ?: throw EntityNotFoundException("flat with id: $flatId not found")
        val tenant = TenantsRepository.findOne(tenantId) ?: throw EntityNotFoundException("tenant with id: $tenantId not found")

        val viewingSlot = flat.schedules
            .flatMap { it.viewingSlots }
            .find { it.id == slotId } ?: throw EntityNotFoundException("slot ith id: $slotId not found")

        val bookedBy = viewingSlot.bookedBy.get()
        if (bookedBy != null && bookedBy.id != tenantId) {
            throw IllegalStateException("it is booked already by someone else")
        }

        viewingSlot.bookedBy.compareAndSet(null, tenant)
    }

}