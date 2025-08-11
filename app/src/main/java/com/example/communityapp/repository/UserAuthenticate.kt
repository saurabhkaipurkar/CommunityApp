package com.example.communityapp.repository

import com.example.communityapp.apiservices.ApiService
import com.example.communityapp.models.ApiResponse
import com.example.communityapp.models.LoginResponse

class UserAuthenticate(private val apiService: ApiService ) {

    suspend fun signupUser(userInfo: Map<String, String>) : ApiResponse{
        return apiService.userInfo(userInfo)
    }

    suspend fun loginAuth(userinfo: Map<String, String>) : LoginResponse {
        return apiService.loginAuth(userinfo)
    }
}