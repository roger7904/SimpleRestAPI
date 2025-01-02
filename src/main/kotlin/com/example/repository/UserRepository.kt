package com.example.repository

import com.example.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {
    fun addUser(name: String, age: Int): User {
        val userId = transaction {
            Users.insert {
                it[Users.name] = name
                it[Users.age] = age
            } get Users.id
        }
        return getUserById(userId)!!
    }

    fun getUserById(id: Int): User? {
        return transaction {
            Users.select { Users.id eq id }
                .map { User(it[Users.id], it[Users.name], it[Users.age]) }
                .singleOrNull()
        }
    }

    fun getAllUsers(): List<User> {
        return transaction {
            Users.selectAll().map {
                User(it[Users.id], it[Users.name], it[Users.age])
            }
        }
    }
}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val age = integer("age")
    override val primaryKey = PrimaryKey(id)
}