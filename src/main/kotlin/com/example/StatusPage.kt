package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            println("StatusPages" + cause.message)
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to cause.message))
        }
        exception<Throwable> { call, cause ->
            println("StatusPages" + cause.message)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Something went wrong"))
        }
    }
}
