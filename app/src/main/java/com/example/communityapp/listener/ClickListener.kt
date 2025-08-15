package com.example.communityapp.listener

import com.example.communityapp.models.Post

interface ClickListener {
    fun likes(postList: Post,position: Int)
    fun comments(postList: Post, position: Int)
    fun share(postList: Post,position: Int)
    fun inAppBrowser(postList: Post,position: Int)
}