package com.booktracker.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String): String = "book_detail/$bookId"
    }
}
