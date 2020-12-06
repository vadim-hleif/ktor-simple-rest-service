package ktor.simple.rest.service.flat

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.jackson.*
import io.ktor.response.*
import io.ktor.routing.*
import ktor.simple.rest.service.flat.utils.FlatsGenerator
import java.text.DateFormat

val flat = FlatsGenerator().generateFlat()

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            registerKotlinModule()
            registerModule(JavaTimeModule())
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
        }
    }
    routing {
        get("/test") { call.respond(flat) }
        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}