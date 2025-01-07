package com.example

import com.example.repository.*
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureAuthRouting() {
    val authService by inject<AuthService>()

    routing {
        post("/register") {
            val params = call.receive<RegisterRequest>()
            try {
                val newUser = authService.register(params.username, params.password, params.role)
                val response = RegisterResponse(
                    id = newUser.id,
                    username = newUser.username,
                    role = newUser.role
                )
                call.respond(response)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val secret = "Roger-TEST-secret"  // TODO move to .env
            val token = authService.login(loginRequest.username, loginRequest.password, secret)
            if (token == null) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            } else {
                call.respond(LoginResponse(token))
            }
        }
    }
}