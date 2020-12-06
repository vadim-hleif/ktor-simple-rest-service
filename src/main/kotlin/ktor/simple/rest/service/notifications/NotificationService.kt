package ktor.simple.rest.service.notifications

import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import ktor.simple.rest.service.tenant.dtos.Tenant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Notifications stub
 * Just prints it to console via logging
 */
object NotificationService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun notifyTenantAboutStatusChanging(viewingSlot: ViewingSlot, flat: Flat, day: LocalDate) = sendNotification(
        "${viewingSlot.bookedBy.get()!!.id}",
        """
                New status of reservation ${viewingSlot.id} is ${viewingSlot.state}.
                
                Details:
                Flat: ${flat.id}
                Time: $day at ${viewingSlot.startTime} - ${viewingSlot.endTime}
            """
    )

    fun notifyLandlordAboutReservation(flat: Flat, day: LocalDate, viewingSlot: ViewingSlot, tenant: Tenant) = sendNotification(
        "${flat.landlord.id}",
        """
                New reservation.
                Please approve or reject it.
                
                Details:
                Flat: ${flat.id}
                Time: $day at ${viewingSlot.startTime} - ${viewingSlot.endTime}
                Tenant: ${tenant.id}
            """
    )

    fun notifyLandlordAboutReleasing(viewingSlot: ViewingSlot, flat: Flat, day: LocalDate, tenant: Tenant) = sendNotification(
        "${flat.landlord.id}",
        """
                Slot reservation was released.
                Time slot is available for future reservations.
                
                Details:
                Flat: ${flat.id}
                Time: $day at ${viewingSlot.startTime} - ${viewingSlot.endTime}
                Previous tenant: ${tenant.id}
            """
    )

    private fun sendNotification(target: String, message: String) {
        logger.info("""
            
            Hello $target,
            
            $message
            
        """.trimIndent())
    }

}