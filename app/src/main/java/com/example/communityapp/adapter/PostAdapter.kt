package com.example.communityapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.communityapp.models.Post
import com.example.communityapp.R
import com.example.communityapp.listener.ClickListener
import com.example.communityapp.util.getRelativeTime

class PostAdapter(private val postList: List<Post>,
                  private val clickListener: ClickListener) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageUser: ImageView = itemView.findViewById(R.id.imageUser)
        val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        val postTime: TextView = itemView.findViewById(R.id.post_time)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)

        val buttonLike: ImageView = itemView.findViewById(R.id.button_like)
        val buttonDislike: ImageView = itemView.findViewById(R.id.button_dislike)
        val textLikeCount: TextView = itemView.findViewById(R.id.textLikeCount)
        val buttonComment: ImageView = itemView.findViewById(R.id.buttonComment)
        val textCommentCount: TextView = itemView.findViewById(R.id.textCommentCount)
        val buttonShare: ImageView = itemView.findViewById(R.id.buttonShare)
        val textShareCount: TextView = itemView.findViewById(R.id.textShareCount)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        holder.textUserName.text = post.user_id
        holder.textDescription.text = post.content
        holder.postTime.text = getRelativeTime(post.created_at)
        holder.textLikeCount.text = post.likeCount.toString()

        if (post.isLiked) {
            holder.buttonLike.visibility = View.VISIBLE
            holder.buttonDislike.visibility = View.GONE
        } else {
            holder.buttonLike.visibility = View.GONE
            holder.buttonDislike.visibility = View.VISIBLE
        }

        holder.buttonLike.setOnClickListener {
            post.isLiked = false
            notifyItemChanged(position)
            clickListener.likes(post, position)
        }

        holder.buttonDislike.setOnClickListener {
            post.isLiked = true
            notifyItemChanged(position)
            clickListener.likes(post, position)
        }

        holder.buttonComment.setOnClickListener {
            clickListener.comments(post,position)
        }

        holder.buttonShare.setOnClickListener {
            clickListener.share(post,position)
        }


        val imageUrl = post.media_file
        if (imageUrl == null) {
            holder.imagePost.visibility = View.GONE
        }
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imagePost)

        // Set user image placeholder
        holder.imageUser.setImageResource(R.drawable.ic_placeholder)
    }

    override fun getItemCount(): Int = postList.size
}