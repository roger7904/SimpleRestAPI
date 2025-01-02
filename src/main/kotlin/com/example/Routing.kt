package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.repository.UserRepository
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val userService by inject<UserService>()

    install(RequestValidation) {
        validate<UserRequest> { user ->
            if (user.name.isBlank()) {
                ValidationResult.Invalid("Name cannot be blank")
            } else if (user.age <= 0) {
                ValidationResult.Invalid("Age must be a positive number")
            } else {
                ValidationResult.Valid
            }
        }
    }

    routing {

        post("/users") {
            val request = call.receive<UserRequest>()
            val user = userService.createUser(request.name, request.age)
            call.respond(user)
        }

        get("/users") {
            val users = userService.listUsers()
            call.respond(users)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            if (request.username == "admin" && request.password == "password") {
                val token = JWT.create()
                    .withAudience("ktorAudience")
                    .withIssuer("ktor.io")
                    .withClaim("username", request.username)
                    .withClaim("role", "admin")
                    .sign(Algorithm.HMAC256("Roger-TEST-secret"))

                call.respond(mapOf("token" to token))
            } else if (request.username == "user" && request.password == "password") {
                val token = JWT.create()
                    .withAudience("ktorAudience")
                    .withIssuer("ktor.io")
                    .withClaim("username", request.username)
                    .withClaim("role", "user")
                    .sign(Algorithm.HMAC256("Roger-TEST-secret"))

                call.respond(mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

    }
}
