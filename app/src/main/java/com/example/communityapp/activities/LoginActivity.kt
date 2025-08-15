package com.example.communityapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.ClientInfo
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.ActivityLoginBinding
import com.example.communityapp.repository.UserAuthenticate
import com.example.communityapp.viewmodel.AuthenticationViewmodel
import com.example.communityapp.viewmodelfactory.AuthenticateFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var authViewModel: AuthenticationViewmodel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is already logged in
        if (ClientInfo.isLoggedIn(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val repository = UserAuthenticate(RetrofitHelper.getApiService())
        val factory = AuthenticateFactory(repository)
        authViewModel = ViewModelProvider(this, factory)[AuthenticationViewmodel::class.java]

        observer()

        binding.googleLogin.setOnClickListener {
            signInWithGoogle()
        }

        binding.btnLogin.setOnClickListener {
            val number = binding.edtphone.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()
            if (number.isNotEmpty() && password.isNotEmpty()) {
                if (phoneNumberValidation(number)) {
                    val userInfo = mapOf(
                        "number" to number,
                        "password" to password
                    )
                    login(userInfo)
                } else {
                    binding.edtphone.error = "Invalid phone number"
                    binding.edtphone.requestFocus()
                }
            } else {
                binding.edtphone.error = "Please enter phone number"
                binding.edtPassword.error = "Please enter password"
            }
        }

        binding.gotoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.userIds.text = ClientInfo.getUserInfo(this)["name"].toString()
    }

    private fun phoneNumberValidation(number: String): Boolean {
        val phoneNumberRegex = "^\\d{10}$"
        return number.matches(phoneNumberRegex.toRegex())
    }

    private fun observer() {
        authViewModel.authResponse.observe(this) {response ->
            if (response.status) {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
                val user = response.data
                ClientInfo.setLogin(this, true)
                ClientInfo.saveUserInfo(
                    context = this,
                    id = user?.id,
                    name = user?.name,
                    email = user?.email,
                    number = user?.number,
                    gender = user?.gender,
                    state_id = user?.state_id,
                    district_id = user?.district_id,
                    taluka_id = user?.taluka_id,
                    address = user?.address,
                    role = user?.role,
                    created_at = user?.created_at
                )
                ClientInfo.userLikedPost(
                    this,
                    Gson().toJson(user?.liked_posts)
                )
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, response.message, Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.authError.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        CoroutineScope(Dispatchers.Main).launch {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.serverClientId))
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val credential = result.credential as? GoogleIdTokenCredential
                val idToken = credential?.idToken

                if (!idToken.isNullOrEmpty()) {
                    firebaseAuthWithGoogle(idToken)
                } else {
                    Toast.makeText(this@LoginActivity, "ID Token is null", Toast.LENGTH_SHORT).show()
                }
            } catch (e: GetCredentialException) {
                Log.e("GoogleSignIn", "Failed: ${e.message}")
                Toast.makeText(this@LoginActivity, "Sign-in cancelled or failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    // Save login session
                    ClientInfo.setLogin(this, true)

                    // You can call your login() if needed for API or directly move to Main
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            }.addOnCanceledListener {
                Toast.makeText(this, "Login Cancelled", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun login(userInfo: Map<String, String>) {
        authViewModel.loginUser(userInfo)
    }
}
