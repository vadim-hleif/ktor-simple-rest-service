package ktor.simple.rest.service.notifications

import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.dtos.ViewingSlot
import ktor.simple.rest.service.tenant.dtos.Tenant
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Notifications stub
 */
object NotificationService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun notifyTenantAboutStatusChanging(viewingSlot: ViewingSlot, flat: Flat, day: LocalDate) = sendNotification(
        "${viewingSlot.bookedBy.get()!!.id}",
        """
                Status of your reservation of flat: ${flat.id} is ${viewingSlot.state}.
                Reminders:
                Landlord is ${flat.landlord.id}. Time in $day at ${viewingSlot.startTime} - ${viewingSlot.endTime}
            """
    )

    fun notifyLandlordAboutReservation(flat: Flat, day: LocalDate, viewingSlot: ViewingSlot, tenant: Tenant) = sendNotification(
        "${flat.landlord.id}",
        """
                New slot reservation on $day at ${viewingSlot.startTime} - ${viewingSlot.endTime} by ${tenant.id}.
                Flat is ${flat.id}
                Please approve or reject it.
            """
    )

    private fun sendNotification(target: String, message: String) {
        logger.info("""
            
            Hello $target,
            
            $message
            
        """.trimIndent())
    }

}