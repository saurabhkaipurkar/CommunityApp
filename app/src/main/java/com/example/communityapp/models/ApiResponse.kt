package com.example.communityapp.models

data class ApiResponse(
    val status: Boolean,
    val message: String,
    val data: Data?
)

data class Data(
    val id : String?,
    val name: String?,
    val email: String?,
    val number: String?,
    val password: String?,
    val role: String?,
    val created_at: String?,
)
