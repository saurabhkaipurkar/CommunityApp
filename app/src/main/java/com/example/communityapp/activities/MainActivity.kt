package com.example.communityapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.communityapp.R
import com.example.communityapp.databinding.ActivityMainBinding
import com.example.communityapp.fragments.EventsFragment
import com.example.communityapp.fragments.HomeFragment
import com.example.communityapp.fragments.CreatePostFragment
import com.example.communityapp.fragments.ProfileFragment
import com.example.communityapp.fragments.QuizFragment
import com.example.communityapp.util.replaceFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
        val allGranted = permissions.all { it.value }
        if(allGranted){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestPermission()
        replaceFragment(supportFragmentManager,R.id.mainFragmentContainer,HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> replaceFragment(supportFragmentManager,R.id.mainFragmentContainer,HomeFragment())
                R.id.nav_event -> replaceFragment(supportFragmentManager,R.id.mainFragmentContainer,EventsFragment())
                R.id.nav_quiz -> replaceFragment(supportFragmentManager,R.id.mainFragmentContainer,QuizFragment())
                R.id.nav_profile -> replaceFragment(supportFragmentManager,R.id.mainFragmentContainer, ProfileFragment())
                R.id.AddPost -> replaceFragment(supportFragmentManager,R.id.mainFragmentContainer, CreatePostFragment())
            }
            true
        }

        binding.chatPage.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolber_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestPermission(){
        permissionLauncher.launch(arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        ))
    }
}