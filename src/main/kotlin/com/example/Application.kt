package com.example

import com.example.tables.UserAccounts
import com.example.tables.Users
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun initDatabase() {
    val dbHost = System.getenv("MYSQL_DB_HOST")
    val dbName = System.getenv("MYSQL_DB_NAME")
    val dbUser = System.getenv("MYSQL_DB_USER")
    val dbPassword = System.getenv("MYSQL_DB_PASSWORD")
    val dbPort = System.getenv("MYSQL_DB_PORT")

    val url = "jdbc:mysql://$dbHost:$dbPort/$dbName?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC"
    val driver = "com.mysql.cj.jdbc.Driver"

    val db = Database.connect(
        url = url,
        driver = driver,
        user = dbUser,
        password = dbPassword
    )

    transaction(db) {
        // init table
        SchemaUtils.create(Users)
        SchemaUtils.create(UserAccounts)
    }
}

fun Application.module() {
    initDatabase()

    configureFrameworks()
    configureStatusPage()
    configureSecurity()
    configureSerialization()
    configureAuthRouting()
    configureRouting()
}
