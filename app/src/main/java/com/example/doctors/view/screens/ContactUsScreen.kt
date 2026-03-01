package com.example.doctors.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ContactUsScreen(navController: NavHostController) {
    // Background gradient
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(LoginBlue.copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 800f
    )

    Scaffold(containerColor = Color.White) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBrush).padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {

                // Top bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                    ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Contact Us", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(24.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header
                        item {
                            Text(text = "ApexMed", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = LoginBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nepal's #1 Health Care App",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                        }

                        // Phone section
                        item {
                            ContactSectionCard(
                                icon = Icons.Default.Call,
                                title = "Give us a Call",
                                details = listOf("+977-1-5970680", "+977-9801165960")
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        // Email section
                        item {
                            ContactSectionCard(
                                icon = Icons.Default.Email,
                                title = "Send us a Message",
                                details = listOf("support@apexmed.com.np")
                            )
                        }
                    }
                }
            }
        }
    }
}

// Contact detail card
@Composable
fun ContactSectionCard(icon: ImageVector, title: String, details: List<String>) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LoginBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Detail items
        details.forEach { detailText ->
            Column(modifier = Modifier.padding(start = 36.dp, top = 4.dp, end = 0.dp, bottom = 4.dp)) {
                Text(
                    text = detailText,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }
}