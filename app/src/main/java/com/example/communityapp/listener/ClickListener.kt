package com.example.communityapp.listener

import com.example.communityapp.models.Post
import com.example.communityapp.models.PostLikes

interface ClickListener {
    fun likes(postList: Post,position: Int)
    fun comments(postList: Post,position: Int)
    fun share(postList: Post,position: Int)
}