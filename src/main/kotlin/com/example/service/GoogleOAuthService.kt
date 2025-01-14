package com.example.service

import com.example.models.UserInfo
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable

suspend fun getUserInfo(
    httpClient: HttpClient,
    token: String
): UserInfo = httpClient.get("https://www.googleapis.com/oauth2/v2/userinfo") {
    headers {
        append(HttpHeaders.Authorization, "Bearer $token")
    }
}.body()

@Serializable
data class UserSession(val state: String, val token: String)