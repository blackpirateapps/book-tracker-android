package com.booktracker.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    NavigationBar(modifier = modifier) {
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { item ->
            val isSelected = if (item is BottomNavItem.Settings) {
                currentDestination?.hierarchy?.any { it.route == Screen.Settings.route } == true
            } else {
                currentDestination?.hierarchy?.any { it.route?.startsWith("home") == true } == true
            }

            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
