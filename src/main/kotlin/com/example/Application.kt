package com.example

import com.example.repository.Users
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

@Serializable
data class UserRequest(val name: String, val age: Int)

@Serializable
data class UserResponse(val id: Int, val name: String, val age: Int)

@Serializable
data class LoginRequest(val username: String, val password: String)

fun initDatabase(config: ApplicationConfig) {
    val url = config.property("storage.jdbcURL").getString()
    val user = config.property("storage.user").getString()
    val password = config.property("storage.password").getString()
    val driver = config.property("storage.driverClassName").getString()

    val db = Database.connect(
        url = url,
        driver = driver,
        user = user,
        password = password
    )

    transaction(db) {
        // init table
        SchemaUtils.create(Users)
    }
}

fun Application.module() {
    initDatabase(environment.config)

    configureFrameworks()
    configureStatusPage()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
