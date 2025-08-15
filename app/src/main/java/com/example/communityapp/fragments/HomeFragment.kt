package com.example.communityapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.communityapp.ClientInfo
import com.example.communityapp.R
import com.example.communityapp.adapter.CommentAdapter
import com.example.communityapp.adapter.PostAdapter
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.FragmentHomeBinding
import com.example.communityapp.listener.ClickListener
import com.example.communityapp.models.Post
import com.example.communityapp.repository.PostRepository
import com.example.communityapp.viewmodel.PostViewModel
import com.example.communityapp.viewmodelfactory.PostViewmodelFactory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.communityapp.util.openCustomTab

class HomeFragment : Fragment(), ClickListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var adapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val repository = PostRepository(RetrofitHelper.getApiService())
        val factory = PostViewmodelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchPost()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerFeed.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(mutableListOf(), this)
        binding.recyclerFeed.adapter = adapter
    }

    private fun fetchPost() {
        postViewModel.getPosts()
        postViewModel.getPostResponse.observe(viewLifecycleOwner) { response ->
            if (response.status && !response.data.isNullOrEmpty()) {
                // Get liked post IDs from saved user info
                val likedPostsJson = ClientInfo.get_liked_post(requireContext())
                val likedPostIds: List<String> = try {
                    if (!likedPostsJson.isNullOrEmpty()) {
                        Gson().fromJson(likedPostsJson, object : TypeToken<List<String>>() {}.type)
                    } else emptyList()
                } catch (e: Exception) {
                    emptyList()
                }

                // Sort posts by date safely
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val sortedPosts = response.data
                    .sortedByDescending {
                        try {
                            dateFormat.parse(it.created_at)?.time
                        } catch (e: Exception) {
                            null
                        }
                    }
                    .map { post ->
                        // Convert post.id to string so comparison works
                        post.isLiked = likedPostIds.contains(post.id)
                        post
                    }

                adapter.updatePosts(sortedPosts)
            }
        }

        postViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }



    private fun observeViewModel() {
        postViewModel.likesResponse.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                fetchPost()
            }
        }

        postViewModel.postComment.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                fetchPost()
            }
        }
    }

    override fun likes(postList: Post, position: Int) {
        val userId = ClientInfo.getUserInfo(requireContext())["id"] ?: return

        postList.isLiked = !postList.isLiked

        // Retrieve existing liked post IDs
        val likedPostsJson = ClientInfo.get_liked_post(requireContext())
        val likedPostIds: MutableList<String> = if (!likedPostsJson.isNullOrEmpty()) {
            Gson().fromJson(likedPostsJson, object : TypeToken<MutableList<String>>() {}.type)
        } else {
            mutableListOf()
        }

        // Add or remove the post ID
        if (postList.isLiked) {
            if (!likedPostIds.contains(postList.id)) likedPostIds.add(postList.id)
        } else {
            likedPostIds.remove(postList.id)
        }

        // Save updated list
        ClientInfo.user_liked_post(requireContext(), Gson().toJson(likedPostIds))

        // Call API
        val likeMap = mapOf(
            "user_id" to userId,
            "new_post_id" to postList.id,
            "likes" to if (postList.isLiked) "1" else "0"
        )
        postViewModel.likes(likeMap)
    }

    override fun comments(postList: Post, position: Int) {
        val userId = ClientInfo.getUserInfo(requireContext())["id"] ?: return

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.comment_dialog, null)

        val commentEditText: EditText = view.findViewById(R.id.editTextMessage)
        val sendButton: ImageView = view.findViewById(R.id.buttonSend)
        val commentsRecycler: RecyclerView = view.findViewById(R.id.recyclerViewComments)

        commentsRecycler.layoutManager = LinearLayoutManager(requireContext())
        val commentAdapter = CommentAdapter(emptyList())
        commentsRecycler.adapter = commentAdapter

        val postId = postList.id?.toIntOrNull() ?: return

        // ✅ Load comments only once when dialog opens
        postViewModel.getComments(postId)
        postViewModel.getComments.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                fetchPost()
                commentAdapter.updateComments(response.data)
            }
        }

        sendButton.setOnClickListener {
            val text = commentEditText.text.toString().trim()
            if (text.isNotEmpty()) {
                val commentMap = mapOf(
                    "user_id" to userId,
                    "new_post_id" to postList.id,
                    "comments" to text
                )

                postViewModel.comments(commentMap)
                commentEditText.text.clear()

                postViewModel.getComments(postId)
                adapter.notifyItemChanged(position)
            }
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }



    override fun share(postList: Post, position: Int) {
        // You can implement sharing later if needed
    }

    override fun inAppBrowser(postList: Post, position: Int) {
        val url = postList.content?.trim() ?: return

        if (url.startsWith("http://") || url.startsWith("https://")) {
            openCustomTab(requireContext(), url)
        }
    }
}
