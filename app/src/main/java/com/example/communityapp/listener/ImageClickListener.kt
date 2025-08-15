package com.example.communityapp.listener

import com.example.communityapp.models.Post

interface ImageClickListener {
    fun imageClick(postList: List<Post>,position: Int)
}