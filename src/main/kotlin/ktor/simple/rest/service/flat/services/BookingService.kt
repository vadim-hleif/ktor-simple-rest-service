package ktor.simple.rest.service.flat.services

import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.notifications.NotificationService
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import ktor.simple.rest.service.utils.exceptions.EntityNotFoundException

object BookingService {

    fun reserveSlot(tenantId: Int, flatId: Int, slotId: Int) {
        val flat = FlatsRepository.findOne(flatId) ?: throw EntityNotFoundException("flat with id: $flatId not found")
        val tenant = TenantsRepository.findOne(tenantId) ?: throw EntityNotFoundException("tenant with id: $tenantId not found")

        val (day, viewingSlot) = flat.schedules
            .map { it.dateOfTheDay to it.viewingSlots }
            .first { (_, slots) -> slots.any { it.id == slotId } }
            .let { (day, slots) -> day to slots.find { it.id == slotId }!! }

        val bookedBy = viewingSlot.bookedBy.get()
        if (bookedBy != null) {
            if (bookedBy.id == tenantId) return
            throw IllegalStateException("it is booked already by someone else")
        }

        if (!viewingSlot.bookedBy.compareAndSet(null, tenant)) {
            throw IllegalStateException("it is booked already by someone else")
        }

        NotificationService.sendNotification(
            "${flat.landlord.id}",
            """
                New time viewing slot reservation on $day at ${viewingSlot.startTime} - ${viewingSlot.endTime} by $tenantId.
                Please approve or reject it.
            """
        )
    }

}