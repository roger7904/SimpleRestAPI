package com.example

import com.example.repository.UserRequest
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
    }
}
