package com.example.repository

import com.example.models.UserAccount
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class UserAccountRepository {
    fun createUserAccount(username: String, password: String, role: String): UserAccount {
        val userId = transaction {
            UserAccounts.insert {
                it[UserAccounts.username] = username
                it[UserAccounts.password] = password
                it[UserAccounts.role] = role
            } get UserAccounts.id
        }
        return getUserAccountById(userId)!!
    }

    fun getUserAccountById(id: Int): UserAccount? {
        return transaction {
            UserAccounts
                .select { UserAccounts.id eq id }
                .map {
                    UserAccount(
                        it[UserAccounts.id],
                        it[UserAccounts.username],
                        it[UserAccounts.password],
                        it[UserAccounts.role]
                    )
                }.singleOrNull()
        }
    }

    fun getUserAccountByUsername(username: String): UserAccount? {
        return transaction {
            UserAccounts
                .select { UserAccounts.username eq username }
                .map {
                    UserAccount(
                        it[UserAccounts.id],
                        it[UserAccounts.username],
                        it[UserAccounts.password],
                        it[UserAccounts.role]
                    )
                }.singleOrNull()
        }
    }
}

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RegisterRequest(val username: String, val password: String, val role: String)

@Serializable
data class LoginResponse(val token: String)

@Serializable
data class RegisterResponse(val id: Int, val username: String, val role: String)

object UserAccounts : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}