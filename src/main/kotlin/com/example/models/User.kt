package com.example.models

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
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val picture: String? = null
)