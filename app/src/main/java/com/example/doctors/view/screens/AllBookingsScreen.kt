package com.example.doctors.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctors.viewmodel.BookingViewModel
import com.example.doctors.model.allSpecialistsList

@Composable
fun AllBookingsScreen(
    navController: NavHostController,
    bookingViewModel: BookingViewModel
) {
    // Handle tab switching
    val navBackStackEntry = navController.currentBackStackEntry
    val initialTabArg = navBackStackEntry?.arguments?.getString("selectedTab")
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(initialTabArg) {
        if (initialTabArg == "specialist") {
            selectedTab = 1
        }
    }

    // Data source
    val bookings = bookingViewModel.bookedAppointments

    // UI background
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2).copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 1500f
    )

    Scaffold(
        containerColor = Color.White,
        bottomBar = { ModernBottomNav(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Bookings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Tab Switcher
                Box(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .height(54.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(27.dp))
                        .padding(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        CustomTabItem(
                            title = "My Appointments",
                            isSelected = selectedTab == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 0 }
                        )
                        CustomTabItem(
                            title = "Our Specialist",
                            isSelected = selectedTab == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTab = 1 }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Content List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (selectedTab == 0) {
                        // My Appointments
                        if (bookings.isEmpty()) {
                            item {
                                Text("No appointments yet.", color = Color.White.copy(0.7f), modifier = Modifier.padding(20.dp))
                            }
                        } else {
                            items(bookings) { appointment ->
                                UpcomingAppointmentCard(
                                    appointment = appointment,
                                    onCancelClick = { bookingViewModel.cancelBooking(appointment) }
                                )
                            }
                        }
                    } else {
                        // Specialist List
                        items(allSpecialistsList) { doctor ->
                            MainSpecialistCard(doctor, navController)
                        }
                    }
                }
            }
        }
    }
}

// Custom Tab UI
@Composable
fun CustomTabItem(title: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(23.dp))
            .clickable { onClick() },
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color(0xFF1976D2) else Color.White
            )
        }
    }
}