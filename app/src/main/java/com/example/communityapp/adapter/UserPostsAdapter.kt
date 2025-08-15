package com.example.communityapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.R
import com.example.communityapp.listener.ImageClicklistener
import com.example.communityapp.models.Post

class UserPostsAdapter(private var userPosts: List<Post>,
    private val clickListener: ImageClicklistener
) : RecyclerView.Adapter<UserPostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_userpost, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = userPosts[position]

        val imageFile = post.media_file
        if (!imageFile.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(imageFile)
                .into(holder.userPost)
        }else{
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }

        holder.userPost.setOnClickListener {
            clickListener.imageClick(userPosts, position)
        }
    }

    override fun getItemCount(): Int = userPosts.size

    // Method to update the list without recreating the adapter
    fun updatePosts(newPosts: List<Post>) {
        userPosts = newPosts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userPost: ImageView = itemView.findViewById(R.id.user_Post)
    }
}