package com.example.communityapp.models

data class TalukaResponse(
    val status: Boolean,
    val message: String,
    val data: List<TalukaData>
)
data class TalukaData(
    val id: Int,
    val state_id: String,
    val district_id: String,
    val taluka_name: String
)
