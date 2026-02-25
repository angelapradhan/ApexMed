package com.example.doctors.view.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.doctors.R
import com.example.doctors.viewmodel.AuthViewModel

val LoginBlue = Color(0xFF1976D2)

@Composable
fun ProfileScreen(navController: NavHostController, authViewModel: AuthViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val authState = authViewModel.state
    val user = authState.currentUser

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { authViewModel.uploadAndSaveProfileImage(context, it) }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(LoginBlue.copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 800f
    )

    // 1. YO STATE THAPNUS (Error hatauna ko lagi)
    var showAboutDialog by remember { mutableStateOf(false) }

    // 2. Dialog lai yaha call garnus
    if (showAboutDialog) {
        AboutAppDialog(onDismiss = { showAboutDialog = false })
    }

    // State management
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 2. Delete Account Confirmation
    // Delete Account Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Account?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        authViewModel.deleteAccount(context) {
                            // Exact Logout ko jastai navigation
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = Color.White,
        // Bottom Navigation thapiyo!
        bottomBar = { ModernBottomNav(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
                    .padding(paddingValues) // Navbar le content nachhopos bhannalaai
                    .padding(horizontal = 24.dp)
            ) {
                // Header (Back button + Title)
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterStart).background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                        ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                        Text(text = "My Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Profile Info Card
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(top = 10.dp), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(contentAlignment = Alignment.BottomEnd) {
                                Image(
                                    painter = if (!user?.profileImageUrl.isNullOrEmpty()) rememberAsyncImagePainter(user?.profileImageUrl) else painterResource(id = R.drawable.profile),
                                    contentDescription = null,
                                    modifier = Modifier.size(110.dp).clip(CircleShape).border(2.dp, LoginBlue.copy(0.1f), CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Box(modifier = Modifier.size(34.dp).background(LoginBlue, CircleShape).border(3.dp, Color.White, CircleShape).clickable { galleryLauncher.launch("image/*") }, contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "${user?.firstName ?: "User"} ${user?.lastName ?: ""}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text(text = user?.email ?: "email@example.com", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }

                item {
                    SectionHeader("General")
                    ProfileMenuCard(listOf(
                        MenuData("Personal details", Icons.Default.Person, onClick = {
                            navController.navigate("personal_details")
                        }),
                        MenuData("Password and security", Icons.Default.Lock, onClick = {
                            navController.navigate("security_screen") // <--- Navigation Working!
                        }),
                        // 1. Favourites ko satta Contact Us
                        MenuData("Contact us", Icons.Default.Call, onClick = {
                            navController.navigate("contact_us")
                        }),
                        // 2. Notification ko satta About App
                        MenuData("About app", Icons.Default.Info, onClick = {
                            showAboutDialog = true // Pop-up kholcha
                        })
                    ))
                }

                item {
                    SectionHeader("Account")
                    ProfileMenuCard(items = listOf(
                        MenuData(
                            title = "Delete account",
                            icon = Icons.Default.Delete,
                            isDangerous = true,
                            onClick = {
                                showDeleteDialog = true // <--- Yo line halnai parchha pop-up ko lagi!
                            }
                        ),
                        MenuData("Logout", Icons.Default.ExitToApp, isDangerous = true, onClick = {
                            authViewModel.logout()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        })
                    ))
                }
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

// Helpers
data class MenuData(
    val title: String,
    val icon: ImageVector,
    val isDangerous: Boolean = false,
    val onClick: () -> Unit = {} // Default khali hunchha
)

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(top = 24.dp, bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun ProfileMenuCard(items: List<MenuData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
    ) {
        Column {
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { item.onClick() } // <--- Yo function call huna jaruri chha
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (item.isDangerous) Color.Red.copy(alpha = 0.1f)
                                else LoginBlue.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = if (item.isDangerous) Color.Red else LoginBlue
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = item.title,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        color = if (item.isDangerous) Color.Red else Color.Black
                    )

                    Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.LightGray)
                }

                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}