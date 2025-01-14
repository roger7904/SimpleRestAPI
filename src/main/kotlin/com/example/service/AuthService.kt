package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.UserAccount
import com.example.repository.UserAccountRepository
import org.springframework.security.crypto.bcrypt.BCrypt

class AuthService(private val userAccountRepository: UserAccountRepository) {

    /**
     * 註冊使用者
     * 對密碼做雜湊後再存入資料庫。
     */
    fun register(username: String, password: String, role: String): UserAccount {
        val existingUser = userAccountRepository.getUserAccountByUsername(username)
        if (existingUser != null) {
            throw IllegalArgumentException("Username already exists.")
        }

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

        val newUser = userAccountRepository.createUserAccount(username, hashedPassword, role)

        return newUser
    }

    /**
     * 登入流程
     * 成功，回傳 JWT token；失敗，回傳 null。
     */
    fun login(username: String, plainPassword: String, secret: String): String? {
        val user = userAccountRepository.getUserAccountByUsername(username) ?: return null

        val matched = BCrypt.checkpw(plainPassword, user.password)
        if (!matched) {
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

    fun issueLocalJWTForOAuthUser(email: String?, name: String?): String {
        // 這裡可以檢查 DB、有無同 email 的使用者，若無就建立
        // 產生 JWT
        val secret = "Roger-TEST-secret"
        val token = JWT.create()
            .withAudience("ktorAudience")
            .withIssuer("ktor.io")
            .withClaim("username", email ?: "unknown")
            .withClaim("role", "user")
            .sign(Algorithm.HMAC256(secret))
        return token
    }
}