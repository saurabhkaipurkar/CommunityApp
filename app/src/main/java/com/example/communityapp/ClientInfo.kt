package com.example.communityapp

import androidx.core.content.edit
import android.content.Context
import android.content.SharedPreferences

object ClientInfo {
    private const val PREF_NAME = "community_app_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_NAME = "user_name"
    private const val KEY_EMAIL = "user_email"
    private const val KEY_PHONE = "user_phone"
    private const val KEY_ROLE = "user_role"
    private const val KEY_CREATED_AT = "user_created_at"
    //private const val KEY_PHOTO_URL = "user_photo_url"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_POST_ID = "post_id"
    private const val KEY_GENDER = "user_gender"
    private const val KEY_STATE_ID = "user_state_id"
    private const val KEY_DISTRICT_ID = "user_district_id"
    private const val KEY_TALUKA_ID = "user_taluka_id"
    private const val KEY_ADDRESS = "user_address"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Set login status
    fun setLogin(context: Context, isLoggedIn: Boolean) {
        getPrefs(context).edit { putBoolean(KEY_IS_LOGGED_IN, isLoggedIn) }
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Save user data
    fun saveUserInfo(
        context: Context, id: String?, name: String?, email: String?, number: String?, role: String?, created_at: String?,
        gender: String?, state_id: String?, district_id: String?, taluka_id: String?, address: String?
    ) {
        getPrefs(context).edit().apply {
            putString(KEY_USER_ID, id)
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, number)
            putString(KEY_ROLE, role)
            putString(KEY_CREATED_AT, created_at)
            putString(KEY_GENDER, gender)
            putString(KEY_STATE_ID, state_id)
            putString(KEY_DISTRICT_ID, district_id)
            putString(KEY_TALUKA_ID, taluka_id)
            putString(KEY_ADDRESS, address)
            apply()
        }
    }

    fun userLikedPost(context: Context, post_id: String?){
        getPrefs(context).edit().apply {
            putString(KEY_POST_ID, post_id)
            apply()
        }
    }

    fun getLikedPost(context: Context): String? {
        return getPrefs(context).getString(KEY_POST_ID, null)
    }

    fun signupDetail(context: Context, name: String?, email: String?, number: String?, role: String?, created_at: String?){
        getPrefs(context).edit().apply {
            putString(KEY_NAME, name)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, number)
            putString(KEY_ROLE, role)
            putString(KEY_CREATED_AT, created_at)
            apply()
        }
    }

    // Get user data
    fun getUserInfo(context: Context): Map<String, String?> {
        val prefs = getPrefs(context)
        return mapOf(
            "id" to prefs.getString(KEY_USER_ID, null),
            "name" to prefs.getString(KEY_NAME, null),
            "email" to prefs.getString(KEY_EMAIL, null),
            "phone" to prefs.getString(KEY_PHONE, null),
            "role" to prefs.getString(KEY_ROLE, null),
            "created_at" to prefs.getString(KEY_CREATED_AT, null),
            "gender" to prefs.getString(KEY_GENDER, null),
            "state_id" to prefs.getString(KEY_STATE_ID, null),
            "district_id" to prefs.getString(KEY_DISTRICT_ID, null),
            "taluka_id" to prefs.getString(KEY_TALUKA_ID, null),
            "address" to prefs.getString(KEY_ADDRESS, null),
            "post_id" to prefs.getString(KEY_POST_ID, null)
            //"photo_url" to prefs.getString(KEY_PHOTO_URL, null)
        )
    }

    fun clearLogin(context: Context) {
        getPrefs(context).edit { clear() }
    }
}
