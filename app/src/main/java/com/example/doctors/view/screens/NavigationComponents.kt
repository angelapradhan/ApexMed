package com.example.doctors.view.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun ModernBottomNav(navController: NavHostController) {
    // get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 35.dp, topEnd = 35.dp),
        color = Color.White,
        shadowElevation = 20.dp
    ) {
        // navigation bar
        NavigationBar(containerColor = Color.Transparent, modifier = Modifier.height(90.dp)) {
            val items = listOf(
                Triple(Icons.Default.Home, "dashboard", "Home"),
                Triple(Icons.Default.DateRange, "all_bookings", "Appointments"),
                Triple(Icons.Default.Person, "profile", "Profile")
            )

            items.forEach { (icon, route, label) ->
                val isSelected = currentRoute == route
                // nav item
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(icon, null, modifier = Modifier.size(28.dp),
                            tint = if (isSelected) Color(0xFF1976D2) else Color.LightGray)
                    },
                    label = {
                        Text(label, fontSize = 12.sp,
                            color = if (isSelected) Color(0xFF1976D2) else Color.Gray)
                    },
                    colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
                )
            }
        }
    }
}