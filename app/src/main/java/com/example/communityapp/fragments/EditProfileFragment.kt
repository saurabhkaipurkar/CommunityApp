package com.example.communityapp.fragments

import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.communityapp.R
import com.example.communityapp.apiservices.RetrofitHelper
import com.example.communityapp.databinding.FragmentEditProfileBinding
import com.example.communityapp.repository.UserProfileRepository
import com.example.communityapp.viewmodel.UserProfileViewModel
import com.example.communityapp.viewmodelfactory.UserProfileFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri
import com.example.communityapp.ClientInfo
import com.example.communityapp.apiservices.apirequest.UpdateDataRequest
import com.example.communityapp.util.convertImageToBase64
import com.example.communityapp.util.setupDistrictSpinner
import com.example.communityapp.util.setupStateSpinner
import com.example.communityapp.util.setupTalukaSpinner
import com.google.gson.Gson

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var userProfileViewModel: UserProfileViewModel
    private var imageUri: Uri? = null
    private var imageFile: File? = null
    private var gender: String = ""
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()){success ->
        if (success) {
            binding.profileImage.setImageURI(imageUri)
        }
    }
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if(uri != null){
            imageUri = uri
            binding.profileImage.setImageURI(uri)

        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(savedInstanceState != null){
            imageUri = savedInstanceState.getString("imageUri")?.toUri()
            savedInstanceState.getString("imageFilePath")?.let {
                imageFile = File(it)
            }
        }
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        imageUri?.let {
            binding.profileImage.setImageURI(it)
        }
        val repository = UserProfileRepository(RetrofitHelper.getApiService())
        val factory = UserProfileFactory(repository)
        userProfileViewModel = ViewModelProvider(this, factory)[UserProfileViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.takeBack.setOnClickListener {
            handleBackPress()
        }
        val info = ClientInfo.getUserInfo(requireContext())
        binding.etName.setText(info["name"] ?: "")

        binding.changePictureLink.paintFlags = binding.changePictureLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.changePictureLink.setOnClickListener {
            openDialog()
        }
        binding.submit.setOnClickListener {
            submitData()
        }
        binding.genderRadioGroup.setOnCheckedChangeListener {_, checkedId ->
            val selectedRadioButton : RadioButton = binding.root.findViewById(checkedId)
            gender = selectedRadioButton.text.toString()
        }

        userProfileViewModel.fetchStates()

        observer()
    }
    private fun handleBackPress() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun openDialog(){
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Change Profile Picture")
            .setMessage("Do you want to change your profile picture?")
            .setPositiveButton("Open Gallery"){_, _ ->
                openGallery()
            }
            .setNegativeButton("Open Camera"){_, _ ->
                openCamera()
            }
            .setCancelable(true)
        val setProfileDialog = builder.create()
        setProfileDialog.window?.setBackgroundDrawableResource(R.drawable.dialog_background)
        setProfileDialog.show()
    }

    private fun openCamera(){
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile!!
        )
        cameraLauncher.launch(imageUri)
    }

    private fun openGallery(){
        galleryLauncher.launch("image/*")
    }

    private fun observer(){
        userProfileViewModel.updateResponse.observe(viewLifecycleOwner){ response ->
            if(response.status){
                Toast.makeText(requireContext(), "${response.message} ", Toast.LENGTH_SHORT).show()
            }
        }
        userProfileViewModel.error.observe(viewLifecycleOwner){ error ->
            Toast.makeText(requireContext(), "$error", Toast.LENGTH_SHORT).show()
        }

        userProfileViewModel.states.observe(viewLifecycleOwner){ stateResponse ->
            if(stateResponse.status){
                setupStateSpinner(
                    requireContext(),
                    binding.stateSpinner,
                    stateResponse.data,
                    onStateSelected = { id ->
                        binding.stateSpinner.tag = id
                        userProfileViewModel.fetchDistricts(id.toInt())
                    }
                )
            }
        }
        userProfileViewModel.districts.observe(viewLifecycleOwner){ districtResponse ->
            if(districtResponse.status){
                setupDistrictSpinner(
                    requireContext(),
                    binding.districtSpinner,
                    districtResponse.data,
                    binding.stateSpinner.tag.toString()
                ) { stateId, districtId ->
                    binding.districtSpinner.tag = districtId
                    userProfileViewModel.fetchTalukas(stateId.toInt(), districtId.toInt())
                }
            }
        }
        userProfileViewModel.talukas.observe(viewLifecycleOwner){ talukaResponse ->
            if(talukaResponse.status){
                setupTalukaSpinner(
                    requireContext(),
                    binding.talukaSpinner,
                    talukaResponse.data,
                    onTalukaSelected = { id, name ->
                        binding.talukaSpinner.tag = id
                    }
                )
            }
        }
    }

    private fun editUserProfile() : UpdateDataRequest {
        val userId = ClientInfo.getUserInfo(requireContext())["id"] ?: ""

        val profileBase64 = if (imageUri != null) {
            convertImageToBase64(requireContext(),imageUri!!)
        } else {
            ""
        }
        return UpdateDataRequest(
            id = userId,
            profile = profileBase64,
            name = binding.etName.text.toString(),
            email = binding.enterEmail.text.toString(),
            number = binding.etPhone.text.toString(),
            password = binding.etPassword.text.toString().ifEmpty { null },
            gender = gender,
            state_id = binding.stateSpinner.tag?.toString() ?: "",
            district_id = binding.districtSpinner.tag?.toString() ?: "",
            taluka_id = binding.talukaSpinner.tag?.toString() ?: "",
            address = binding.etAddress.text.toString()
        )
    }

    private fun submitData(){
        if (binding.stateSpinner.tag == null ||
            binding.districtSpinner.tag == null ||
            binding.talukaSpinner.tag == null) {
            Toast.makeText(requireContext(), "Please select state, district, and taluka", Toast.LENGTH_SHORT).show()
            return
        }
        val dataMap = editUserProfile()
        val jsonString = Gson().toJson(dataMap)
        userProfileViewModel.updateProfileData(jsonString)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        imageUri?.let { outState.putString("imageUri", it.toString()) }
        imageFile?.let { outState.putString("imageFilePath", it.absolutePath) }
    }
}