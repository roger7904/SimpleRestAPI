package com.example

import com.example.repository.*
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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

    routing {
        authenticate("auth-jwt") {
            get("/role-check") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                when (role) {
                    "admin" -> {
                        call.respond(
                            mapOf(
                                "message" to "Hi, Admin! 這裡是只有管理員能看到的資訊。"
                            )
                        )
                    }
                    "user" -> {
                        call.respond(
                            mapOf(
                                "message" to "Hello, User! 這裡是一般使用者能看到的內容。"
                            )
                        )
                    }
                    else -> {
                        call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                    }
                }
            }
        }
    }
}