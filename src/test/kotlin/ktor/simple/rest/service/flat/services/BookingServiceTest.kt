package ktor.simple.rest.service.flat.services

import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookingServiceTest {

    @Test
    fun `free slot -- success`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
    }

    @Test
    fun `double reserving of the same slot with the same tenant -- success`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
    }

    @Test
    fun `busy slot -- error`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val anotherTenant = TenantsRepository.findAll().first { it.id != tenant.id }
        assertThrows<IllegalStateException> {
            BookingService.reserveSlot(tenantId = anotherTenant.id, flatId = flat.id, slotId = slot.id)
        }
    }

}