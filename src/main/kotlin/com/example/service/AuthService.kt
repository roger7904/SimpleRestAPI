package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.UserAccount
import com.example.repository.UserAccountRepository

class AuthService(private val userAccountRepository: UserAccountRepository) {

    // TODO password salt hash
    fun register(username: String, password: String, role: String): UserAccount {
        val existingUser = userAccountRepository.getUserAccountByUsername(username)
        if (existingUser != null) {
            throw IllegalArgumentException("Username already exists.")
        }

        val newUser = userAccountRepository.createUserAccount(username, password, role)
        return newUser
    }


    fun login(username: String, password: String, secret: String): String? {
        val user = userAccountRepository.getUserAccountByUsername(username) ?: return null

        // TODO 驗證鹽雜湊密碼
        if (user.password != password) {
            return null
        }

        val token = JWT.create()
            .withAudience("ktorAudience")
            .withIssuer("ktor.io")
            .withClaim("username", user.username)
            .withClaim("role", user.role)
            .sign(Algorithm.HMAC256(secret))

        return token
    }
}