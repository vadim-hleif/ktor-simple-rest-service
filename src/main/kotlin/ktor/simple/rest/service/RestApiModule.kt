package ktor.simple.rest.service

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.flat.services.BookingService.changeSlotStatus
import ktor.simple.rest.service.flat.services.BookingService.releaseSlot
import ktor.simple.rest.service.flat.services.BookingService.reserveSlot
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import ktor.simple.rest.service.utils.exceptions.EntityNotFoundException
import ktor.simple.rest.service.utils.requests.ViewingSlotPatchRequest
import java.text.DateFormat

fun Application.module() {
    install(StatusPages) {
        exception<EntityNotFoundException> {
            call.respond(HttpStatusCode.NotFound, mapOf("message" to it.message))
        }
        exception<IllegalStateException> {
            call.respond(HttpStatusCode.Forbidden, mapOf("message" to it.message))
        }
        exception<IllegalArgumentException> {
            call.respond(HttpStatusCode.BadRequest, mapOf("message" to it.message))
        }
        exception<Exception> {
            call.respond(HttpStatusCode.InternalServerError, mapOf("message" to "unhandled exception: ${it}"))
        }
    }
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        }
    }
    routing {
        get("/flats") {
            call.respond(FlatsRepository.findAll())
        }
        get("/tenants") {
            call.respond(TenantsRepository.findAll())
        }
        patch("/flats/{flat_id}/slots/{slot_id}") {
            val flatId = call.parameters["flat_id"]!!.toInt()
            val slotId = call.parameters["slot_id"]!!.toInt()
            val patchRequest = call.receive<ViewingSlotPatchRequest>()

            when {
                patchRequest.tenantId != null -> reserveSlot(tenantId = patchRequest.tenantId, flatId = flatId, slotId = slotId)
                patchRequest.state != null -> changeSlotStatus(state = patchRequest.state, flatId = flatId, slotId = slotId)
            }

            call.respond(status = HttpStatusCode.NoContent, "")
        }
        delete("/flats/{flat_id}/slots/{slot_id}") {
            val flatId = call.parameters["flat_id"]!!.toInt()
            val slotId = call.parameters["slot_id"]!!.toInt()

            releaseSlot(flatId, slotId)

            call.respond(status = HttpStatusCode.NoContent, "")
        }
    }
}