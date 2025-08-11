package com.example.communityapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.models.ApiResponse
import com.example.communityapp.models.LoginResponse
import com.example.communityapp.repository.UserAuthenticate
import kotlinx.coroutines.launch

class AuthenticationViewmodel(private val repository: UserAuthenticate) : ViewModel() {

    private val _signupResponse = MutableLiveData<ApiResponse>()
    val signupResponse: LiveData<ApiResponse> = _signupResponse

    //=================== Global For viewmodel Error ======================
    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> = _authError

    fun signupUser(userInfo: Map<String, String>){
        viewModelScope.launch {
            try {
                val response = repository.signupUser(userInfo)
                _signupResponse.value = response
            }catch (e: Exception){
                _authError.value = e.message
            }
        }
    }

    private val _authResponse = MutableLiveData<LoginResponse>()
    val authResponse: LiveData<LoginResponse> = _authResponse

    fun loginUser(userInfo: Map<String, String>){
        viewModelScope.launch {
            try {
                val response = repository.loginAuth(userInfo)
                _authResponse.value = response
            }catch (e: Exception){
                _authError.value = e.message
            }
        }
    }


}