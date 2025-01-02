package com.example

import com.example.repository.Users
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    initDatabase()

    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

@Serializable
data class UserRequest(val name: String, val age: Int)

@Serializable
data class UserResponse(val id: Int, val name: String, val age: Int)

@Serializable
data class LoginRequest(val username: String, val password: String)

fun initDatabase() {
    val db = Database.connect(
        url = "jdbc:postgresql://localhost:5432/ktor_db",
        driver = "org.postgresql.Driver",
        user = "postgres",
    )

    transaction(db) {
        // init table
        SchemaUtils.create(Users)
    }
}

fun Application.module() {
    configureFrameworks()
    configureStatusPage()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
