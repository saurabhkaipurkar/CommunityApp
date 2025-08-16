package com.example.communityapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.communityapp.ClientInfo
import com.example.communityapp.R
import com.example.communityapp.activities.FullScreenImageActivity
import com.example.communityapp.adapter.UserPostsAdapter
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.FragmentProfileBinding
import com.example.communityapp.listener.ImageClickListener
import com.example.communityapp.models.Post
import com.example.communityapp.repository.PostRepository
import com.example.communityapp.repository.UserProfileRepository
import com.example.communityapp.viewmodel.PostViewModel
import com.example.communityapp.viewmodel.UserProfileViewModel
import com.example.communityapp.viewmodelfactory.PostViewmodelFactory
import com.example.communityapp.viewmodelfactory.UserProfileFactory

class ProfileFragment : Fragment(), ImageClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var userProfileViewModel: UserProfileViewModel
    private lateinit var userPostsAdapter: UserPostsAdapter
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize ViewModels
        setupViewModels()

        // Setup RecyclerView
        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data
        observeUserProfile()
        observeUserPosts()

        // Setup click listeners
        setupClickListeners()
    }

    private fun setupViewModels() {
        val userRepository = UserProfileRepository(RetrofitHelper.getApiService())
        val userFactory = UserProfileFactory(userRepository)
        userProfileViewModel = ViewModelProvider(this, userFactory)[UserProfileViewModel::class.java]

        val postRepository = PostRepository(RetrofitHelper.getApiService())
        val postFactory = PostViewmodelFactory(postRepository)
        postViewModel = ViewModelProvider(this, postFactory)[PostViewModel::class.java]
    }

    private fun setupRecyclerView() {
        binding.recyclerUserPosts.layoutManager = GridLayoutManager(requireContext(), 3)
        userPostsAdapter = UserPostsAdapter(emptyList(),this)
        binding.recyclerUserPosts.adapter = userPostsAdapter
    }

    private fun setupClickListeners() {
        binding.buttonEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeUserProfile() {
        val userId = ClientInfo.getUserInfo(requireContext())["id"]
        if (userId != null) {
            userProfileViewModel.getUserProfile(userId.toInt())
        }

        userProfileViewModel.userProfile.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                val user = response.data
                if (user.isNotEmpty()) {
                    binding.textUsername.text = user[0].name
                    binding.textEmail.text = user[0].email
                }
            }
        }

        userProfileViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Profile Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeUserPosts() {
        postViewModel.getPosts()

        postViewModel.getPostResponse.observe(viewLifecycleOwner) { response ->
            if (response.status) {
                val currentUserId = ClientInfo.getUserInfo(requireContext())["id"]
                val userPosts = response.data.filter {
                    it.user_id == currentUserId
                }
                // Use the adapter's update method instead of creating new adapter
                userPostsAdapter.updatePosts(userPosts)
            }
        }

        postViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Posts Error: $error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun imageClick(postList: List<Post>, position: Int) {
        val allImageUrls = postList.mapNotNull { it.media_file?.takeIf { url -> url.isNotEmpty() } }

        if (allImageUrls.isNotEmpty()) {
            val intent = Intent(requireContext(), FullScreenImageActivity::class.java).apply {
                putStringArrayListExtra("image_urls", ArrayList(allImageUrls))
                putExtra("current_position", position) // clicked image's index in the list
            }
            startActivity(intent)
        }
    }
}