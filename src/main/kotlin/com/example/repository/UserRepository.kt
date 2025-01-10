package com.example.repository

import com.example.models.User
import com.example.tables.Users
import kotlinx.serialization.Serializable
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

@Serializable
data class UserRequest(val name: String, val age: Int)

@Serializable
data class UserResponse(val id: Int, val name: String, val age: Int)