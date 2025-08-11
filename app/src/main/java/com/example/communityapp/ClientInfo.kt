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
        context: Context, id: String?, name: String?, email: String?, number: String?, role: String?, created_at: String?
    ) {
        getPrefs(context).edit().apply {
            putString(KEY_USER_ID, id)
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
            "created_at" to prefs.getString(KEY_CREATED_AT, null)
            //"photo_url" to prefs.getString(KEY_PHOTO_URL, null)
        )
    }

    fun clearLogin(context: Context) {
        getPrefs(context).edit { clear() }
    }
}
