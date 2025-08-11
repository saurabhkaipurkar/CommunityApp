package com.example.communityapp.models

data class LoginResponse(
    val status: Boolean,
    val message: String,
    val data : LoginData?
)
data class LoginData(
    val id : String,
    val name : String,
    val email : String,
    val number : String,
    val password : String,
    val role : String,
    val created_at : String
)
