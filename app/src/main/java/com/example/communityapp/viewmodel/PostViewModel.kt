package com.example.communityapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.communityapp.models.GetPostResponse
import com.example.communityapp.models.PostComments
import com.example.communityapp.models.PostLikes
import com.example.communityapp.models.PostResponse
import com.example.communityapp.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel(){

    private val _postResponse = MutableLiveData<PostResponse>()
    val postResponse: LiveData<PostResponse> = _postResponse

    // error for global
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // sending post
    fun sendPost(content: Map<String, String>){
        viewModelScope.launch {
            try {
                val response = repository.sendPost(content)
                _postResponse.value = response
            } catch (e: Exception) {
                // Handle error
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    private val _getPostResponse = MutableLiveData<GetPostResponse>()
    val getPostResponse: LiveData<GetPostResponse> = _getPostResponse

    //receiving post
    fun getPosts(){
        viewModelScope.launch {
            try {
                val response = repository.getPost()
                _getPostResponse.value = response
            } catch (e: Exception) {
                // Handle error
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    private val _likesResponse = MutableLiveData<PostLikes>()
    val likesResponse: LiveData<PostLikes> = _likesResponse

    fun likes (likes: Map<String, String>){
        viewModelScope.launch {
            try {
                val response = repository.likes(likes)
                _likesResponse.value = response
            }catch (e: Exception){
                _error.value = e.message ?: "Unknown error"
            }
        }
    }

    private val _postComment = MutableLiveData<PostComments>()
    val postComment: LiveData<PostComments> = _postComment

    fun comments(comments: Map<String, String>){
        viewModelScope.launch {
            try {
                val response = repository.comments(comments)
                _postComment.value = response
            }catch (e: Exception){
                _error.value = e.message ?: "Unknown error"
            }
        }
    }
}