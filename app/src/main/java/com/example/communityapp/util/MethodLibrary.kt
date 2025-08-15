package com.example.communityapp.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Base64
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.communityapp.models.DistrictData
import com.example.communityapp.models.StateData
import com.example.communityapp.models.TalukaData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.net.toUri

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

fun setupStateSpinner(
    context: Context,
    spinner: Spinner,
    states: List<StateData>,
    onStateSelected: (String) -> Unit
) {
    val stateNames = mutableListOf<String>()
    stateNames.add("Select State") // Add hint at 0th position
    stateNames.addAll(states.map { it.state_name })

    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, stateNames)
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) { // Skip the hint item at position 0
                val selectedState = states[position - 1] // Adjust index for actual data
                onStateSelected(selectedState.id)
            } else {
                // Position 0 is selected (hint text), clear the tag
                spinner.tag = null
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            spinner.tag = null
        }
    }
}

fun setupDistrictSpinner(
    context: Context,
    spinner: Spinner,
    districts: List<DistrictData>,
    stateId: String,
    onDistrictSelected: (Int, Int) -> Unit
) {
    val districtNames = mutableListOf<String>()
    districtNames.add("Select District") // Add hint at 0th position
    districtNames.addAll(districts.map { it.district_name })

    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, districtNames)
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) { // Skip the hint item at position 0
                val selectedDistrict = districts[position - 1] // Adjust index for actual data
                onDistrictSelected(stateId.toInt(), selectedDistrict.id)
            } else {
                // Position 0 is selected (hint text), clear the tag
                spinner.tag = null
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            spinner.tag = null
        }
    }
}

fun setupTalukaSpinner(
    context: Context,
    spinner: Spinner,
    talukas: List<TalukaData>,
    onTalukaSelected: (Int, String) -> Unit
) {
    val talukaNames = mutableListOf<String>()
    talukaNames.add("Select Taluka") // Add hint at 0th position
    talukaNames.addAll(talukas.map { it.taluka_name })

    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, talukaNames)
    spinner.adapter = adapter

    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (position > 0) { // Skip the hint item at position 0
                val selectedTaluka = talukas[position - 1] // Adjust index for actual data
                onTalukaSelected(selectedTaluka.id, selectedTaluka.taluka_name)
            } else {
                // Position 0 is selected (hint text), clear the tag
                spinner.tag = null
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {
            spinner.tag = null
        }
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
    } catch (_: Exception) {
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

fun isInternetAvailable(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val activeNetwork = cm.getNetworkCapabilities(network) ?: return false
    return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

fun startLoadingBar(context: Context){
    ProgressBar(context).apply {
        visibility = View.VISIBLE
    }
}
fun stopLoadingBar(context: Context){
    ProgressBar(context).apply {
        visibility = View.GONE
    }
}

fun openCustomTab(context: Context, url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true) // Show website title in toolbar
        .setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .build()
    // This skips Android’s default intent resolution (so no YouTube app)
    customTabsIntent.intent.setPackage("com.android.chrome")

    try {
        customTabsIntent.launchUrl(context, url.toUri())
    } catch (_: Exception) {
        // If Chrome not installed, open in default browser
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }
}