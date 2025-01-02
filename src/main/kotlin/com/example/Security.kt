package com.example

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {
    install(Authentication) {
        jwt("auth-jwt") {
            val secret = "Roger-TEST-secret"
            val issuer = "ktor.io"
            val audience = "ktorAudience"
            val myRealm = "Access to 'protected' routes"

            realm = myRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withIssuer(issuer)
                    .withAudience(audience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("username").asString().isNotEmpty()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }

    routing {
        authenticate("auth-jwt") {
            get("/admin") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (role == "admin") {
                    call.respond(mapOf("message" to "Welcome, Admin!"))
                } else {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                }
            }

            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                val role = principal?.payload?.getClaim("role")?.asString()

                if (role == "user" || role == "admin") {
                    call.respond(mapOf("message" to "Welcome, User!"))
                } else {
                    call.respond(HttpStatusCode.Forbidden, mapOf("error" to "Access denied"))
                }
            }
        }
    }
}
