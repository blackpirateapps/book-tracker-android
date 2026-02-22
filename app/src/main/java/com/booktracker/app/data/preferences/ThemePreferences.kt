package com.booktracker.app.data.preferences

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false) // Default light theme
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var apiDomain: String
        get() = prefs.getString(KEY_API_DOMAIN, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_DOMAIN, value).apply()

    var apiPassword: String
        get() = prefs.getString(KEY_API_PASSWORD, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_PASSWORD, value).apply()

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_MODE = "is_dark_mode"
        private const val KEY_API_DOMAIN = "api_domain"
        private const val KEY_API_PASSWORD = "api_password"
    }
}
