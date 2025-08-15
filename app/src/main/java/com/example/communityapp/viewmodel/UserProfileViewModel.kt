package com.example.communityapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.apiservices.apirequest.UpdateDataRequest
import com.example.communityapp.models.DistrictResponse
import com.example.communityapp.models.GetUserProfile
import com.example.communityapp.models.StateResponse
import com.example.communityapp.models.TalukaResponse
import com.example.communityapp.models.UserProfileResponse
import com.example.communityapp.repository.UserProfileRepository
import kotlinx.coroutines.launch

class UserProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    private val _updateResponse = MutableLiveData<UserProfileResponse>()
    val updateResponse: MutableLiveData<UserProfileResponse> = _updateResponse

    private val _error = MutableLiveData<String>()
    val error: MutableLiveData<String> = _error

    fun updateProfileData(jsonString: String) {
        viewModelScope.launch {
            try {
                val response = repository.giveProfileData(jsonString)
                _updateResponse.value = response
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    //================= Get User Profile =============================================

    private val _userProfile = MutableLiveData<GetUserProfile>()
    val userProfile: MutableLiveData<GetUserProfile> = _userProfile

    fun getUserProfile(userId: Int){
        viewModelScope.launch {
            try {
                val response = repository.getUserProfile(userId)
                _userProfile.value = response
            }catch (e: Exception){
                _error.value = e.message
            }
        }
    }

    //=================================================================================================

    private val _states = MutableLiveData<StateResponse>()
    val states: LiveData<StateResponse> = _states

    private val _districts = MutableLiveData<DistrictResponse>()
    val districts: LiveData<DistrictResponse> = _districts

    private val _talukas = MutableLiveData<TalukaResponse>()
    val talukas: LiveData<TalukaResponse> = _talukas

    fun fetchStates() {
        viewModelScope.launch  {
            try {
                _states.value = repository.getState()
            }catch (e: Exception){
                _error.value = e.message
            }

        }

    }

    fun fetchDistricts(stateId: Int) {
        viewModelScope.launch {
            try {
                _districts.value = repository.getDistrict(stateId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun fetchTalukas(districtId: Int, stateId: Int) {
        viewModelScope.launch {
            try {
                _talukas.value = repository.getTaluka(districtId, stateId)
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
}