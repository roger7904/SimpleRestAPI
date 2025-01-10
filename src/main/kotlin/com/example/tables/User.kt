package com.example.tables

import org.jetbrains.exposed.sql.Table

object UserAccounts : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val age = integer("age")
    override val primaryKey = PrimaryKey(id)
}