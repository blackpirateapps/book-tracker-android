package com.booktracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.booktracker.app.data.preferences.ThemePreferences
import com.booktracker.app.data.repository.MockBookRepository
import com.booktracker.app.presentation.navigation.AppNavigation
import com.booktracker.app.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {

    // Repository would be injected with Hilt later
    private val repository = MockBookRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themePreferences = ThemePreferences(this)

        setContent {
            var isDarkMode by remember { mutableStateOf(themePreferences.isDarkModeEnabled) }

            AppTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        repository = repository,
                        themePreferences = themePreferences,
                        isDarkMode = isDarkMode,
                        onThemeChanged = { isDark ->
                            isDarkMode = isDark
                            themePreferences.isDarkModeEnabled = isDark
                        }
                    )
                }
            }
        }
    }
}
