package com.example.communityapp.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.repository.UserAuthenticate
import com.example.communityapp.viewmodel.AuthenticationViewmodel

class AuthenticateFactory(private val repository: UserAuthenticate) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AuthenticationViewmodel::class.java)){
            return AuthenticationViewmodel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}