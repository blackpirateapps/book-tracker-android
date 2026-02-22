package com.booktracker.app.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.booktracker.app.presentation.navigation.Screen

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    data object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    data object History : BottomNavItem("History", Icons.Default.History, Screen.History.route)
    data object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.History,
    BottomNavItem.Settings
)

@Composable
fun AppBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = navBackStackEntry?.destination

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = if (item is BottomNavItem.Settings) {
                    currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true
                } else if (item is BottomNavItem.History) {
                    currentDestination?.hierarchy?.any { it.route == Screen.History.route } == true
                } else {
                    currentDestination?.hierarchy?.any { it.route?.startsWith("home") == true } == true
                }

                val tint = if (isSelected) {
                    Color(0xFF007AFF)
                } else {
                    Color(0xFF8E8E93)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 6.dp)
                        .clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                ) {
                    Icon(imageVector = item.icon, contentDescription = item.title, tint = tint)
                    Text(text = item.title, color = tint)
                }
            }
        }
    }
}
