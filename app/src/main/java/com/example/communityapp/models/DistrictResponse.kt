package com.example.communityapp.models

data class DistrictResponse(
    val status: Boolean,
    val message: String,
    val data: List<DistrictData>
)
data class DistrictData(
    val id: Int,
    val state_id: String,
    val district_name: String
)
