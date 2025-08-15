package com.example.communityapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.R
import com.example.communityapp.models.CommentData
import com.example.communityapp.util.getRelativeTime

class CommentAdapter(
    private var commentList: List<CommentData>?
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        return CommentViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comments, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: CommentViewHolder,
        position: Int
    ) {
        val comment = commentList?.getOrNull(position)
        holder.commentTextView.text = comment?.comments?.takeIf { it.isNotBlank() } ?: ""
        holder.usernameTextView.text = comment?.user_id?.takeIf { it.isNotBlank() } ?: ""
        holder.timestampTextView.text = getRelativeTime(comment?.created_at ?: "")
    }

    override fun getItemCount(): Int = commentList?.size ?: 0

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentTextView: TextView = itemView.findViewById(R.id.view_comment)
        val usernameTextView: TextView = itemView.findViewById(R.id.username)
        val timestampTextView: TextView = itemView.findViewById(R.id.timestamp)
        val imageAvatar: ImageView = itemView.findViewById(R.id.user_avatar)
    }

    fun updateComments(newComments: List<CommentData>){
        commentList = newComments
        notifyDataSetChanged()
    }
}