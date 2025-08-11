package com.example.communityapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.communityapp.ClientInfo
import com.example.communityapp.adapter.PostAdapter
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.FragmentHomeBinding
import com.example.communityapp.listener.ClickListener
import com.example.communityapp.models.Post
import com.example.communityapp.repository.PostRepository
import com.example.communityapp.viewmodel.PostViewModel
import com.example.communityapp.viewmodelfactory.PostViewmodelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment(), ClickListener {

    private lateinit var binding: FragmentHomeBinding
    private  lateinit var postViewModel: PostViewModel
    private lateinit var adapter : PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val repository = PostRepository(RetrofitHelper.getApiService())
        val factory = PostViewmodelFactory(repository)
        postViewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]

        setupRecyclerView()
        observer()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postViewModel.getPosts()
    }

    private fun setupRecyclerView() {
        binding.recyclerFeed.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(emptyList(),this)
        binding.recyclerFeed.adapter = adapter
    }

    private fun observer(){
        postViewModel.getPostResponse.observe(viewLifecycleOwner){response ->
            if(response.status){
                if (response.data.isNotEmpty()){
                    val data = response.data
                    val filteredData =  data.sortedByDescending {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val date = dateFormat.parse(it.created_at)
                        date?.time
                    }
                    adapter = PostAdapter(filteredData,this)
                    binding.recyclerFeed.adapter = adapter
                }
            }
        }
        postViewModel.error.observe(viewLifecycleOwner){error ->
            Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
        }

        postViewModel.likesResponse.observe(viewLifecycleOwner){response ->
            if(response.status){
                Toast.makeText(requireContext(), "${response.message} ", Toast.LENGTH_SHORT).show()
            }
        }
        postViewModel.postComment.observe(viewLifecycleOwner){response ->
            if(response.status){
                Toast.makeText(requireContext(), "${response.message} ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun likes(postList: Post, position: Int) {
        val userId = ClientInfo.getUserInfo(requireContext())["id"]
        if(userId != null){
            val newLikeState = !postList.isLiked // toggle current state
            postList.likeCount = (postList.likeCount + if (newLikeState) -1 else 1).coerceAtLeast(0)

            // Update UI instantly
            adapter.notifyItemChanged(position)

            val likes = mapOf(
                "user_id" to userId,
                "new_post_id" to postList.id,
                "likes" to if (newLikeState) "Yes" else "No"
            )
            postViewModel.likes(likes)
        }
    }

    override fun comments(postList: Post, position: Int) {
    }

    override fun share(postList: Post, position: Int) {

    }
}