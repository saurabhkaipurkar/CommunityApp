package com.example.communityapp.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.ClientInfo
import com.example.communityapp.R
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.FragmentCreatePostBinding
import com.example.communityapp.repository.PostRepository
import com.example.communityapp.util.replaceFragment
import com.example.communityapp.viewmodel.PostViewModel
import com.example.communityapp.viewmodelfactory.PostViewmodelFactory

class CreatePostFragment : Fragment() {
    private lateinit var binding: FragmentCreatePostBinding
    private lateinit var postViewModel: PostViewModel
    private var imageUri: Uri? = null // Changed to nullable Uri

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.selectedImages.setImageURI(uri)
                binding.selectedImages.visibility = View.VISIBLE // Show selected image
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        val repository = PostRepository(RetrofitHelper.getApiService())
        val factory = PostViewmodelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.galleryButton.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        binding.submitButton.setOnClickListener {
            postContent()
        }

        observePostResponse()
    }

    private fun postContent() {
        val postText = binding.postEditText.text.toString().trim()

        // Validate that user has entered either text or selected an image
        if (postText.isEmpty() && imageUri == null) {
            Toast.makeText(requireContext(), "Please enter text or select an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        binding.submitButton.isEnabled = false
        binding.submitButton.text = "Posting..."

        // Encode image to base64 if image is selected
        val mediaValue = if (imageUri != null) {
            encodeImageToBase64(imageUri!!)
        } else {
            "" // Empty string if no image
        }

        // Send the post
        sendPost(postText, mediaValue)
    }

    private fun encodeImageToBase64(uri: Uri): String {
        return try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                val base64Image = Base64.encodeToString(bytes, Base64.NO_WRAP)
                "data:image/png;base64,$base64Image"
            } ?: ""
        } catch (e: Exception) {
            Log.e("CreatePost", "Error encoding image to base64", e)
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
            ""
        }
    }

    private fun sendPost(contentValue: String, mediaValue: String) {
        val userId = ClientInfo.getUserInfo(requireContext())["id"]
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in. Please login again.", Toast.LENGTH_SHORT).show()
            // Navigate to login screen or handle accordingly
            return
        }
        val contentMap = mapOf(
            "content" to contentValue,
            "media" to mediaValue,
            "user_id" to userId
        )

        Log.d("CreatePost", "sendPost: $contentMap")
        postViewModel.sendPost(contentMap)
    }

    private fun observePostResponse() {
        postViewModel.postResponse.observe(viewLifecycleOwner) { response ->
            // Reset button state
            binding.submitButton.isEnabled = true
            binding.submitButton.text = "Post"

            if (response.status) {
                Toast.makeText(requireContext(), "${response.message} ", Toast.LENGTH_SHORT).show()

                // Clear the form
                binding.postEditText.setText("")
                binding.selectedImages.setImageURI(null)
                binding.selectedImages.visibility = View.GONE
                imageUri = null

                // Navigate back to posts list or wherever appropriate
                replaceFragment(parentFragmentManager,R.id.mainFragmentContainer, HomeFragment())

            } else {
                Toast.makeText(requireContext(), "${response.message} ", Toast.LENGTH_SHORT).show()
            }
        }

        // Also observe for any errors from the ViewModel
        postViewModel.error.observe(viewLifecycleOwner) { error ->
            binding.submitButton.isEnabled = true
            binding.submitButton.text = "Post"

            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
            Log.e("CreatePost", "ViewModel error: $error")
        }
    }
}