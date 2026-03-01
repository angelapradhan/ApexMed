package com.example.doctors.view.screens


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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctors.viewmodel.AuthViewModel
import com.example.doctors.viewmodel.UserViewModel


// theme colors
val LoginBlue = Color(0xFF1976D2)
val ThemeBlack = Color(0xFF1A1A1A)
val MediumGrey = Color(0xFF424242)
val SoftGrey = Color(0xFFF5F5F5)
val DarkGrey = Color(0xFF757575)

@Composable
fun ProfileScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    // get user data
    val userState = userViewModel.state
    val user = userState.currentUser

    // background gradient
    val gradientBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(LoginBlue.copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 800f
    )

    // dialog states
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AboutAppDialog(onDismiss = { showAboutDialog = false })
    }

    // delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete Account?", fontWeight = FontWeight.Bold) },
            text = { Text(text = "Are you sure you want to delete your account? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        userViewModel.deleteAccount(context) {
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
        bottomBar = { ModernBottomNav(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
            ) {
                // Header
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.align(Alignment.CenterStart).background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                        ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                        Text(text = "My Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // Profile Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = SoftGrey,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.padding(20.dp),
                                    tint = MediumGrey
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "${user?.firstName ?: "User"} ${user?.lastName ?: ""}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = ThemeBlack
                            )
                            Text(
                                text = user?.email ?: "email@example.com",
                                color = DarkGrey,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Menu Section: General
                item {
                    SectionHeader("General")
                    ProfileMenuCard(listOf(
                        MenuData("Personal details", Icons.Default.Person, onClick = {
                            navController.navigate("personal_details")
                        }),
                        MenuData("Password and security", Icons.Default.Lock, onClick = {
                            navController.navigate("security_screen")
                        }),
                        MenuData("Contact us", Icons.Default.Call, onClick = {
                            navController.navigate("contact_us")
                        }),
                        MenuData("About app", Icons.Default.Info, onClick = {
                            showAboutDialog = true
                        })
                    ))
                }

                // Menu Section: Account
                item {
                    SectionHeader("Account")
                    ProfileMenuCard(items = listOf(
                        MenuData(
                            title = "Delete account",
                            icon = Icons.Default.Delete,
                            isDangerous = true,
                            onClick = {
                                showDeleteDialog = true
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
    val onClick: () -> Unit = {}
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
                        .clickable { item.onClick() }
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