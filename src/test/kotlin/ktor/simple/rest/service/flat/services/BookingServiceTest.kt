package ktor.simple.rest.service.flat.services

import io.kotest.matchers.shouldBe
import io.mockk.mockkObject
import io.mockk.verify
import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.flat.dtos.SlotState.APPROVED
import ktor.simple.rest.service.flat.dtos.SlotState.REJECTED
import ktor.simple.rest.service.notifications.NotificationService
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookingServiceTest {

    @AfterEach
    internal fun tearDown() = FlatsRepository.findAll().values
        .flatMap { it.schedules }
        .flatMap { it.viewingSlots }
        .forEach {
            it.bookedBy.set(null)
            it.state = null
        }

    @Test
    fun `reserve free slot -- success`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
    }

    @Test
    fun `double reserving of the same slot with the same tenant -- success, only one notification`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        mockkObject(NotificationService)
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        verify(exactly = 1) { NotificationService.notifyLandlordAboutReservation(any(), any(), any(), any()) }
    }

    @Test
    fun `reserve busy slot -- error`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val anotherTenant = TenantsRepository.findAll().first { it.id != tenant.id }
        assertThrows<IllegalStateException> {
            BookingService.reserveSlot(tenantId = anotherTenant.id, flatId = flat.id, slotId = slot.id)
        }
    }

    @Test
    fun `approve busy slot -- success`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val changeSlotStatus = BookingService.changeSlotStatus(state = APPROVED, flatId = flat.id, slotId = slot.id)
        changeSlotStatus.state shouldBe APPROVED
    }

    @Test
    fun `double approve busy slot -- success, only one notification`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()
        mockkObject(NotificationService)

        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val changeSlotStatus = BookingService.changeSlotStatus(state = APPROVED, flatId = flat.id, slotId = slot.id)
        BookingService.changeSlotStatus(state = APPROVED, flatId = flat.id, slotId = slot.id)

        changeSlotStatus.state shouldBe APPROVED
        verify(exactly = 1) { NotificationService.notifyTenantAboutStatusChanging(any(), any(), any()) }
    }

    @Test
    fun `approve free slot -- error`() {
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()

        assertThrows<IllegalArgumentException> {
            BookingService.changeSlotStatus(state = APPROVED, flatId = flat.id, slotId = slot.id)
        }
    }

    @Test
    fun `reject busy slot -- success`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val changeSlotStatus = BookingService.changeSlotStatus(state = REJECTED, flatId = flat.id, slotId = slot.id)
        changeSlotStatus.state shouldBe REJECTED
    }

    @Test
    fun `double reject busy slot -- success, only one notification`() {
        val tenant = TenantsRepository.findAll().first()
        val flat = FlatsRepository.findAll().values.first()
        val slot = flat.schedules.first().viewingSlots.first()
        mockkObject(NotificationService)
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val changeSlotStatus = BookingService.changeSlotStatus(state = REJECTED, flatId = flat.id, slotId = slot.id)
        BookingService.changeSlotStatus(state = REJECTED, flatId = flat.id, slotId = slot.id)
        changeSlotStatus.state shouldBe REJECTED

        verify(exactly = 1) { NotificationService.notifyTenantAboutStatusChanging(any(), any(), any()) }
    }

    @Test
    fun `double reserve of rejected slot -- error`() {
        val tenant = TenantsRepository.findAll().last()
        val flat = FlatsRepository.findAll().values.last()
        val slot = flat.schedules.first().viewingSlots.last()
        BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)

        val changeSlotStatus = BookingService.changeSlotStatus(state = REJECTED, flatId = flat.id, slotId = slot.id)
        changeSlotStatus.state shouldBe REJECTED

        assertThrows<IllegalArgumentException> {
            BookingService.reserveSlot(tenantId = tenant.id, flatId = flat.id, slotId = slot.id)
        }
    }


}