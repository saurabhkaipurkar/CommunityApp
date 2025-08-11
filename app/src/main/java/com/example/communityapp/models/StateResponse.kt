package com.example.communityapp.models

data class StateResponse(
    val status: Boolean,
    val message: String,
    val data: List<StateData>
)
data class StateData(
    val id: String,
    val state_name: String
)
