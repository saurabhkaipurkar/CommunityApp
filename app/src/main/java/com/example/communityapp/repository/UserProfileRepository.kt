package com.example.communityapp.repository

import com.example.communityapp.apiservices.ApiService
import com.example.communityapp.models.DistrictResponse
import com.example.communityapp.models.GetUserProfile
import com.example.communityapp.models.StateResponse
import com.example.communityapp.models.TalukaResponse
import com.example.communityapp.models.UserProfileResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class UserProfileRepository(private val apiService: ApiService) {

    suspend fun giveProfileData(jsonString: String): UserProfileResponse {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonString.toRequestBody(mediaType)
        return apiService.updateUserInfo(requestBody)
    }
    suspend fun getUserProfile(userId: Int): GetUserProfile {
        return apiService.getUserProfile(userId)
    }

    suspend fun getState(): StateResponse {
        return apiService.state()
    }
    suspend fun getDistrict(stateId: Int): DistrictResponse {
        return apiService.district(stateId)
    }
    suspend fun getTaluka(districtId: Int, stateId: Int): TalukaResponse {
        return apiService.taluka(districtId, stateId)
    }
}