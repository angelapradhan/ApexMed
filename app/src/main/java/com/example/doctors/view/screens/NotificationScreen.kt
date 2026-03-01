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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.doctors.model.NotificationModel
import com.example.doctors.viewmodel.NotificationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavHostController,
    notificationViewModel: NotificationViewModel
) {
    // fetch data
    LaunchedEffect(Unit) {
        notificationViewModel.fetchNotifications()
    }

    // state
    val notificationsList = notificationViewModel.notificationsList
    var isTodayExpanded by remember { mutableStateOf(false) }

    // ui brush
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2).copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 1000f
    )

    Scaffold(containerColor = Color.White) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBrush)) {
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

                // header
                NotificationHeader(navController)

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)
                ) {
                    // today section
                    val todayNotifications = notificationsList.filter {
                        it.type.trim().equals("Today", ignoreCase = true)
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NotificationSectionHeader(title = "Today", isOnBlue = true)

                            // see more logic
                            if (todayNotifications.size > 3) {
                                Text(
                                    text = if (isTodayExpanded) "See Less" else "See More",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { isTodayExpanded = !isTodayExpanded }
                                )
                            }
                        }
                    }

                    if (todayNotifications.isEmpty()) {
                        item { Text("No notifications today", color = Color.White.copy(0.7f)) }
                    } else {
                        val displayToday = if (isTodayExpanded) todayNotifications else todayNotifications.take(3)
                        items(displayToday) { notification ->
                            NotificationItemCard(notification)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // this week section
                    item { NotificationSectionHeader(title = "This Week", isOnBlue = false) }

                    val weekNotifications = notificationsList.filter {
                        val type = it.type.trim()
                        type.equals("This Week", ignoreCase = true) ||
                                type.equals("Yesterday", ignoreCase = true)
                    }

                    if (weekNotifications.isEmpty()) {
                        item { Text("No notifications this week", color = Color.Gray) }
                    } else {
                        val sortedWeek = weekNotifications.sortedByDescending { it.timestamp }
                        items(sortedWeek) { notification ->
                            NotificationItemCard(notification)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

// sub comppnents

@Composable
fun NotificationHeader(navController: NavHostController) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text("Notifications", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
@Composable
fun NotificationSectionHeader(title: String, isOnBlue: Boolean) {
    Text(
        text = title,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 18.sp,
        color = if (isOnBlue) Color.White else Color.Black.copy(alpha = 0.7f),
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
fun NotificationItemCard(notification: NotificationModel) {
    // notification card
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color(0xFFE9F0FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            // text content
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)

                Text(
                    text = if (notification.doctorName.isNotEmpty())
                        "${notification.message} ${notification.doctorName}"
                    else notification.message,
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                if (notification.appointmentTime.isNotEmpty()) {
                    Text(
                        text = "Scheduled at: ${notification.appointmentTime}",
                        color = Color(0xFF1976D2),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // time
            Text(
                text = notification.timeAgo,
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}