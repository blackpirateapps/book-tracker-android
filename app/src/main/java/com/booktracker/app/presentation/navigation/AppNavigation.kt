package com.booktracker.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.booktracker.app.domain.usecase.AddBookUseCase
import com.booktracker.app.domain.usecase.GetBookByIdUseCase
import com.booktracker.app.domain.usecase.GetBooksUseCase
import com.booktracker.app.domain.usecase.UpdateBookUseCase
import com.booktracker.app.data.preferences.ThemePreferences
import com.booktracker.app.domain.repository.BookRepository
import com.booktracker.app.domain.usecase.*
import com.booktracker.app.presentation.components.AppBottomNavigation
import com.booktracker.app.domain.model.ShelfType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import com.booktracker.app.presentation.screens.*
import com.booktracker.app.presentation.viewmodel.*
import com.booktracker.app.presentation.refresh.AppRefreshBus

@Composable
fun AppNavigation(
    repository: BookRepository,
    themePreferences: ThemePreferences,
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    // Create use cases (would be injected with Hilt later)
    val getBooksUseCase = remember { GetBooksUseCase(repository) }
    val getBookByIdUseCase = remember { GetBookByIdUseCase(repository) }
    val addBookUseCase = remember { AddBookUseCase(repository) }
    val updateBookUseCase = remember { UpdateBookUseCase(repository) }

    Scaffold(
        bottomBar = {
            // Only show bottom bar on top level destinations
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute?.startsWith("home") == true || currentRoute == Screen.Settings.route) {
                AppBottomNavigation(navController = navController)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.createRoute(), // Default to all shelves
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(tween(300)) { it } + fadeIn(tween(300))
            },
            exitTransition = {
                slideOutHorizontally(tween(300)) { -it / 3 } + fadeOut(tween(150))
            },
            popEnterTransition = {
                slideInHorizontally(tween(300)) { -it / 3 } + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(tween(300)) { it } + fadeOut(tween(150))
            }
        ) {
            composable(
                route = Screen.Home.route,
                arguments = listOf(navArgument("shelf") { 
                    type = NavType.StringType
                    nullable = true 
                    defaultValue = null
                })
            ) { backStackEntry ->
                val shelfArg = backStackEntry.arguments?.getString("shelf")
                val viewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(backStackEntry.savedStateHandle, getBooksUseCase)
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                HomeScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        viewModel.onEvent(event)
                    },
                    onBookClick = { bookId ->
                        navController.navigate(Screen.BookDetail.createRoute(bookId))
                    },
                    onForceRefresh = {
                        AppRefreshBus.trigger()
                        viewModel.onEvent(HomeEvent.OnRefresh)
                    },
                    onSettingsClick = {
                        navController.navigate(Screen.Settings.route)
                    }
                )

                // Add Book Modal Sheet
                if (uiState.showAddSheet) {
                    AddBookDialog(
                        addBookUseCase = addBookUseCase,
                        onDismiss = { viewModel.onEvent(HomeEvent.OnDismissAddSheet) }
                    )
                }
            }

            composable(Screen.Settings.route) {
                var apiDomain by remember { mutableStateOf(themePreferences.apiDomain) }
                var apiPassword by remember { mutableStateOf(themePreferences.apiPassword) }

                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    isDarkModeEnabled = isDarkMode,
                    onToggleDarkMode = onThemeChanged,
                    apiDomain = apiDomain,
                    onApiDomainChanged = {
                        apiDomain = it
                        themePreferences.apiDomain = it
                    },
                    apiPassword = apiPassword,
                    onApiPasswordChanged = {
                        apiPassword = it
                        themePreferences.apiPassword = it
                    },
                    onTestConnection = { repository.testConnection() },
                    onForceRefresh = { AppRefreshBus.trigger() }
                )
            }

            composable(
                route = Screen.BookDetail.route,
                arguments = listOf(navArgument("bookId") { type = NavType.StringType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: return@composable
                val viewModel: BookDetailViewModel = viewModel(
                    factory = BookDetailViewModel.Factory(
                        bookId = bookId,
                        getBookByIdUseCase = getBookByIdUseCase,
                        updateBookUseCase = updateBookUseCase
                    )
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                BookDetailScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun AddBookDialog(
    addBookUseCase: AddBookUseCase,
    onDismiss: () -> Unit
) {
    val viewModel: AddBookViewModel = viewModel(
        factory = AddBookViewModel.Factory(addBookUseCase)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            AddBookScreen(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onDismiss = onDismiss
            )
        }
    }
}
