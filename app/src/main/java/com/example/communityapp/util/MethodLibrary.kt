package com.example.communityapp.util

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.communityapp.models.DistrictData
import com.example.communityapp.models.StateData
import com.example.communityapp.models.TalukaData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun convertImageToBase64(context: Context, imageUri: Uri): String {
    return try {
        context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
            val bytes = inputStream.readBytes()
            Base64.encodeToString(bytes, Base64.DEFAULT)
        } ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun setupTalukaSpinner(
    context: Context,
    spinner: Spinner,
    talukas: List<TalukaData>,
    onTalukaSelected: (id: String, name: String) -> Unit
) {
    val talukaName = talukas.map { it.taluka_name }
    val talukaIds = talukas.map { it.id }

    val adapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_dropdown_item,
        talukaName
    )
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val selectedId = talukaIds[position].toString()
            val selectedName = talukaName[position]
            onTalukaSelected(selectedId, selectedName)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun setupStateSpinner(
    context: Context,
    spinner: Spinner,
    states: List<StateData>,
    onStateSelected: (stateId: String) -> Unit
) {
    val stateNames = states.map { it.state_name }
    val stateIds = states.map { it.id }

    val adapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_dropdown_item,
        stateNames
    )
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val selectedStateId = stateIds[position]
            spinner.tag = selectedStateId
            onStateSelected(selectedStateId)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun setupDistrictSpinner(
    context: Context,
    spinner: Spinner,
    districts: List<DistrictData>,
    stateId: String,
    onDistrictSelected: (stateId: String, districtId: String) -> Unit
) {
    val districtNames = districts.map { it.district_name }
    val districtIds = districts.map { it.id }

    val adapter = ArrayAdapter(
        context,
        android.R.layout.simple_spinner_dropdown_item,
        districtNames
    )
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            val selectedDistrictId = districtIds[position].toString()
            spinner.tag = selectedDistrictId
            onDistrictSelected(stateId, selectedDistrictId)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}

fun getRelativeTime(timestamp: String): String {
    return try {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val postTime = dateFormat.parse(timestamp)
        val currentTime = Date()

        if (postTime == null) return "Unknown time"

        val timeDifference = currentTime.time - postTime.time
        val seconds = timeDifference / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365

        when {
            years > 0 -> "${years}y ago"
            months > 0 -> "${months}mo ago"
            weeks > 0 -> "${weeks}w ago"
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            seconds > 30 -> "${seconds}s ago"
            else -> "Just now"
        }
    } catch (e: Exception) {
        "Unknown time"
    }
}

fun replaceFragment(
    fragmentManager: FragmentManager,
    containerId: Int,
    fragment: Fragment
) {
    fragmentManager.beginTransaction()
        .replace(containerId, fragment)
        .addToBackStack(null)
        .commit()
}