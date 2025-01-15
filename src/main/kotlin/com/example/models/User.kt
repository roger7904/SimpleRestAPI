package com.example.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(val id: Int, val name: String, val age: Int)

@Serializable
data class UserAccount(
    val id: Int,
    val username: String,
    val password: String,
    val role: String
)

@Serializable
data class UserInfo(
    val id: String,
    val name: String,
    @SerialName("given_name") val givenName: String,
    @SerialName("family_name") val familyName: String,
    val picture: String,
    val locale: String? = null
)