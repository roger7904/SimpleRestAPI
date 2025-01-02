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

fun initDatabase() {
    val dbHost = System.getenv("DB_HOST") ?: "localhost"
    val dbName = System.getenv("DB_NAME") ?: "ktor_db"
    val dbUser = System.getenv("DB_USER") ?: "postgres"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"
    val dbPort = System.getenv("DB_PORT") ?: "5432"  // 視需求

    val url = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    val driver = "org.postgresql.Driver"

    val db = Database.connect(
        url = url,
        driver = driver,
        user = dbUser,
        password = dbPassword
    )

    transaction(db) {
        // init table
        SchemaUtils.create(Users)
    }
}

fun Application.module() {
    initDatabase()

    configureFrameworks()
    configureStatusPage()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
