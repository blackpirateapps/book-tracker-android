package com.booktracker.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.booktracker.app.presentation.navigation.Screen

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    data object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    data object Settings : BottomNavItem("Settings", Icons.Default.Settings, Screen.Settings.route)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Settings
)

@Composable
fun AppBottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = navBackStackEntry?.destination

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = if (item is BottomNavItem.Settings) {
                    currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true
                } else {
                    currentDestination?.hierarchy?.any { it.route?.startsWith("home") == true } == true
                }

                val tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 4.dp)
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
                    Text(text = item.title, style = MaterialTheme.typography.labelSmall, color = tint)
                }
            }
        }
    }
}
