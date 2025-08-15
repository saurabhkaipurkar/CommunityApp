package com.example.communityapp.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.communityapp.adapter.FullScreenImageAdapter
import com.example.communityapp.databinding.ActivityFullScreenImageBinding

class FullScreenImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var imageAdapter: FullScreenImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager()
    }

    private fun setupViewPager() {
        // Get image URLs and current position from intent
        val imageUrls = intent.getStringArrayListExtra("image_urls") ?: arrayListOf()
        val currentPosition = intent.getIntExtra("current_position", 0)

        // Setup adapter
        imageAdapter = FullScreenImageAdapter(imageUrls)
        binding.viewPager.adapter = imageAdapter

        // Set current item to the selected position
        binding.viewPager.setCurrentItem(currentPosition, false)

        // Optional: Add page indicator or position counter
        updatePositionCounter(currentPosition, imageUrls.size)

        // Optional: Listen to page changes
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updatePositionCounter(position, imageUrls.size)
            }
        })
    }

    private fun updatePositionCounter(position: Int, total: Int) {
        // Update position counter if you have one in your layout
        binding.positionCounter.text = "${position + 1} / $total"
    }
}