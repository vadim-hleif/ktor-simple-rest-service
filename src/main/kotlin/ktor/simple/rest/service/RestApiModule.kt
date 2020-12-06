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
import ktor.simple.rest.service.utils.exceptions.EntityNotFoundException
import ktor.simple.rest.service.flat.dao.FlatsRepository
import ktor.simple.rest.service.flat.services.BookingService
import ktor.simple.rest.service.utils.requests.BookSlotRequest
import ktor.simple.rest.service.tenant.dao.TenantsRepository
import java.text.DateFormat

fun Application.module() {
    install(StatusPages) {
        exception<EntityNotFoundException> {
            call.respond(HttpStatusCode.NotFound, mapOf("message" to it.message))
        }
        exception<IllegalStateException> {
            call.respond(HttpStatusCode.Forbidden, mapOf("message" to it.message))
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
            val tenantId = call.receive<BookSlotRequest>().tenantId

            BookingService.reserveSlot(tenantId = tenantId, flatId = flatId, slotId = slotId)

            call.respond(status = HttpStatusCode.NoContent, "")
        }
    }
}