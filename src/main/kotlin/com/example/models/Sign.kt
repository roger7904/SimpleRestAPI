package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class SignDTO(
    val signAccount: String,
    val signPassword: String
)