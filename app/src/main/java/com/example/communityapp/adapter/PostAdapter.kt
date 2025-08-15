package com.example.communityapp.adapter

import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.example.communityapp.models.Post
import com.example.communityapp.R
import com.example.communityapp.listener.ClickListener
import com.example.communityapp.util.getRelativeTime

class PostAdapter(
    private var posts: MutableList<Post>, // Changed to MutableList
    private val clickListener: ClickListener
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageUser: ImageView = itemView.findViewById(R.id.imageUser)
        val textUserName: TextView = itemView.findViewById(R.id.textUserName)
        val postTime: TextView = itemView.findViewById(R.id.post_time)
        val imagePost: ImageView = itemView.findViewById(R.id.imagePost)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val likeCheckBox: Chip = itemView.findViewById(R.id.likeCheckBox)
        val textCommentCount: Chip = itemView.findViewById(R.id.click_to_comment)
        val buttonShare: Chip = itemView.findViewById(R.id.buttonShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.textUserName.text = post.user_id

        holder.textDescription.text = post.content
        holder.textDescription.autoLinkMask = Linkify.WEB_URLS
        holder.textDescription.movementMethod = LinkMovementMethod.getInstance()

        holder.textCommentCount.text = post.comment_count

        holder.postTime.text = getRelativeTime(post.created_at)

        // Set like button state and count
        holder.likeCheckBox.isChecked = post.isLiked
        holder.likeCheckBox.text = post.like_count

        holder.textDescription.setOnClickListener {
            clickListener.inAppBrowser(post, position)
        }

        // Handle like button click - keeping your original logic
        holder.likeCheckBox.setOnClickListener {
            clickListener.likes(post, position)
        }

        holder.textCommentCount.setOnClickListener {
            clickListener.comments(post ,position)
        }

        holder.buttonShare.setOnClickListener {
            clickListener.share(post, position)
        }


        // ✅ FIX: Reset visibility and only load if not null/empty
        val imageUrl = post.media_file
        if (imageUrl.isNullOrEmpty()) {
            holder.imagePost.visibility = View.GONE
        } else {
            holder.imagePost.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imagePost)
        }
    }

    override fun getItemCount(): Int = posts.size

    // Method to update the entire posts list
    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }
}