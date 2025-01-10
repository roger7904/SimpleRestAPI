package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class AllBookDTO(
    val allNo: Int? = null,
    val allName: String,
    val allAuthor: String,
    val allBookFrom: String,
    val allUrl: String
)