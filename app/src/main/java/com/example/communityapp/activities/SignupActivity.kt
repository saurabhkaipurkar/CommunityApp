package com.example.communityapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.ClientInfo
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.ActivitySignupBinding
import com.example.communityapp.repository.UserAuthenticate
import com.example.communityapp.viewmodel.AuthenticationViewmodel
import com.example.communityapp.viewmodelfactory.AuthenticateFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var authViewModel: AuthenticationViewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = UserAuthenticate(RetrofitHelper.getApiService())
        val factory = AuthenticateFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthenticationViewmodel::class.java]

        observer()

        binding.btnSignUp.setOnClickListener {
            signUp()
        }


        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun observer() {
        authViewModel.signupResponse.observe(this) { response ->
            if (response.status) {

                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                val user = response.data
                ClientInfo.setLogin(this, true)
                ClientInfo.saveUserInfo(
                    this,
                    id = user?.id,
                    name = user?.name,
                    email = user?.email,
                    number = user?.number,
                    role = user?.role,
                    created_at = user?.created_at
                )
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }
        authViewModel.authError.observe(this) { response ->
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }

    private fun signUp() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (validatePassword(password)) {
                if (password == confirmPassword) {
                    val userInfo = mapOf(
                        "name" to name,
                        "email" to email,
                        "number" to phone,
                        "password" to password
                    )
                    giveToApi(userInfo)
                } else {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                binding.etPassword.error = "Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and be at least 8 characters long."
                binding.etPassword.requestFocus()
            }
        }else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }
    private fun giveToApi(userInfo: Map<String, String>) {
        authViewModel.signupUser(userInfo)
    }

    private fun validatePassword(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{6,}$"
        return password.matches(passwordRegex.toRegex())
    }
}