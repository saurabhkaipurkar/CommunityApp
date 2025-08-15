package com.example.communityapp.repository

import com.example.communityapp.apiservices.ApiService
import com.example.communityapp.models.CommentsResponse
import com.example.communityapp.models.GetPostResponse
import com.example.communityapp.models.PostComments
import com.example.communityapp.models.PostLikes
import com.example.communityapp.models.PostResponse

class PostRepository(private val apiService: ApiService) {

    suspend fun sendPost(content: Map<String, String>): PostResponse {
        return apiService.createPost(content)

    }

    suspend fun getPost() : GetPostResponse {
        return apiService.getPosts()
    }
    suspend fun likes(content: Map<String, String>): PostLikes {
        return apiService.likes(content)
    }
    suspend fun comments(content: Map<String, String>): PostComments {
        return apiService.comments(content)
    }

    suspend fun getComments(postId: Int): CommentsResponse {
        return apiService.getComments(postId)
    }


}