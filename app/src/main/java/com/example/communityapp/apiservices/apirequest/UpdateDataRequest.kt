package com.example.communityapp.apiservices.apirequest

data class UpdateDataRequest(
    val id: String,
    val profile: String,
    val name: String,
    val email: String,
    val number: String,
    val password: String?,
    val gender: String,
    val state_id: String,
    val district_id: String,
    val taluka_id: String,
    val address: String
)