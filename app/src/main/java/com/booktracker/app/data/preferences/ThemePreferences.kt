package com.booktracker.app.data.preferences

import android.content.Context
import android.content.SharedPreferences

class ThemePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isDarkModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false) // Default light theme
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_DARK_MODE = "is_dark_mode"
    }
}
