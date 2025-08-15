package com.example.communityapp.models

data class UserProfileResponse(
    val status: Boolean,
    val message: String
)

data class GetUserProfile(
    val status: Boolean,
    val message: String,
    val data: List<UserProfile>
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val number: String,
    val password: String,
    val gender: String,
    val state_id: String,
    val district_id: String,
    val taluka_id: String,
    val address: String,
    val profile: String?, // nullable
    val role: String,
    val topic: String?,   // nullable
    val created_at: String
)
