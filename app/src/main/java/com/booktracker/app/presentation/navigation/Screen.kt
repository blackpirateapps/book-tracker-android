package com.booktracker.app.presentation.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home?shelf={shelf}") {
        fun createRoute(shelf: String? = null): String {
            return if (shelf != null) "home?shelf=$shelf" else "home"
        }
    }
    data object Settings : Screen("settings")
    data object BookDetail : Screen("book_detail/{bookId}") {
        fun createRoute(bookId: String): String = "book_detail/$bookId"
    }
}
