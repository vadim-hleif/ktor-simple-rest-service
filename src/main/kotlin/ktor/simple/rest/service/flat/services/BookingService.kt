package ktor.simple.rest.service.flat.services

import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.SlotState
import ktor.simple.rest.service.flat.dtos.SlotState.REJECTED
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import ktor.simple.rest.service.notifications.NotificationService
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import ktor.simple.rest.service.utils.exceptions.EntityNotFoundException

object BookingService {

    fun reserveSlot(tenantId: Int, flatId: Int, slotId: Int) {
        val flat = FlatsRepository.findOne(flatId) ?: throw EntityNotFoundException("flat with id: $flatId not found")
        val tenant = TenantsRepository.findOne(tenantId) ?: throw EntityNotFoundException("tenant with id: $tenantId not found")

        val (day, viewingSlot) = findSlot(flat, slotId)

        val bookedBy = viewingSlot.bookedBy.get()
        if (bookedBy != null) {
            // can't be reserved after rejection
            if (viewingSlot.state == REJECTED) throw IllegalArgumentException("it was rejected and won't be used anymore")
            // skip the same actions (for avoiding extra notifications)
            if (bookedBy.id == tenantId) return

            // can't be reserved by another person, if someone reserved it
            throw IllegalStateException("it is reserved already by someone else")
        }

        // for case when someone reserve it in parallel
        if (!viewingSlot.bookedBy.compareAndSet(null, tenant))
            throw IllegalStateException("it is reserved already by someone else")

        NotificationService.notifyLandlordAboutReservation(flat, day, viewingSlot, tenant)
    }

    fun changeSlotStatus(state: SlotState, flatId: Int, slotId: Int): ViewingSlot {
        val flat = FlatsRepository.findOne(flatId) ?: throw EntityNotFoundException("flat with id: $flatId not found")

        val (day, viewingSlot) = findSlot(flat, slotId)

        // is case when we try change status of free slot
        if (viewingSlot.bookedBy.get() == null) throw IllegalArgumentException("slot $viewingSlot isn't booked by someone")
        // skip the same actions (for avoiding extra notifications)
        if (viewingSlot.state == state) return viewingSlot
        // for case when we try to approve rejected slot
        if (viewingSlot.state == REJECTED) throw IllegalArgumentException("it was rejected and won't be used anymore")

        viewingSlot.state = state
        NotificationService.notifyTenantAboutStatusChanging(viewingSlot, flat, day)

        return viewingSlot
    }

    fun releaseSlot(flatId: Int, slotId: Int) {
        val flat = FlatsRepository.findOne(flatId) ?: throw EntityNotFoundException("flat with id: $flatId not found")
        val (day, viewingSlot) = findSlot(flat, slotId)

        // is case when we try to release status a free slot
        val prevTenant = viewingSlot.bookedBy.get() ?: throw IllegalArgumentException("slot is free")
        // for case when we try to release rejected slot
        if (viewingSlot.state == REJECTED) throw IllegalArgumentException("it was rejected and won't be used anymore")

        viewingSlot.bookedBy.set(null)
        viewingSlot.state = null

        NotificationService.notifyLandlordAboutReleasing(viewingSlot, flat, day, prevTenant)
    }

    private fun findSlot(flat: Flat, slotId: Int) = flat.schedules
        .map { it.dateOfTheDay to it.viewingSlots }
        .first { (_, slots) -> slots.any { it.id == slotId } }
        .let { (day, slots) -> day to slots.find { it.id == slotId }!! }
}