package com.booktracker.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.booktracker.app.data.repository.MockBookRepository
import com.booktracker.app.presentation.navigation.AppNavigation
import com.booktracker.app.presentation.theme.AppTheme

class MainActivity : ComponentActivity() {

    // Repository would be injected with Hilt later
    private val repository = MockBookRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IOSTheme {
                AppNavigation(
                    repository = repository,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
