package com.example.communityapp.models

data class PostResponse(
    val status: Boolean,
    val message: String
)

data class GetPostResponse(
    val status: Boolean,
    val message: String,
    val data: List<Post>
)
data class Post(
    val id: String,
    val user_id: String,
    val media_file: String?,
    val content: String?,
    val created_at: String,
    var isLiked: Boolean = false, // default unliked
    var likeCount: Int = 0
)

data class PostLikes(
    val status: Boolean,
    val message: String
)

data class PostComments(
    val status: Boolean,
    val message: String
)
