package com.example.doctors.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctors.viewmodel.UserViewModel


@Composable
fun PersonalDetailsScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    // state management
    val userState = userViewModel.state
    val user = userState.currentUser
    val context = androidx.compose.ui.platform.LocalContext.current

    // editable form fields
    var name by remember { mutableStateOf("${user?.firstName ?: ""} ${user?.lastName ?: ""}") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var userName by remember { mutableStateOf(user?.userName ?: "") }
    var phoneNumber by remember { mutableStateOf(user?.contact ?: "") }

    // header gradient
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF1976D2).copy(alpha = 0.8f), Color.White),
        startY = 0f,
        endY = 800f
    )

    Scaffold(containerColor = Color.White) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(gradientBrush).padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {

                // header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape).size(40.dp)
                        ) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Personal Details", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // details form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            // 1. Name
                            DetailInputField(label = "Name", value = name, onValueChange = { name = it })
                            Spacer(modifier = Modifier.height(20.dp))

                            // 2. Email
                            DetailInputField(
                                label = "E mail address",
                                value = email,
                                onValueChange = {},
                                enabled = false,
                                onDisabledClick = {
                                    android.widget.Toast.makeText(context, "Email can't be changed!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))

                            // 3. User name
                            DetailInputField(label = "User name", value = userName, onValueChange = { userName = it })
                            Spacer(modifier = Modifier.height(20.dp))

                            // 4. Phone number
                            DetailInputField(
                                label = "Phone number",
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                isPhone = true
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            // save button
                            Button(
                                onClick = {
                                    // update in database
                                    userViewModel.updateUserDetails(name, userName, phoneNumber, context)
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                            ) {
                                Text("Save Changes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// sub components

@Composable
fun DetailInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPhone: Boolean = false,
    enabled: Boolean = true,
    onDisabledClick: () -> Unit = {}
) {
    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth().clickable(enabled = !enabled) { onDisabledClick() }) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    disabledContainerColor = Color(0xFFEEEEEE),
                    disabledTextColor = Color.Gray,

                    focusedContainerColor = Color(0xFFF7F8F9),
                    unfocusedContainerColor = Color(0xFFF7F8F9),
                    focusedTextColor = Color.Gray,
                    unfocusedTextColor = Color.Gray,

                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                leadingIcon = if (isPhone) {
                    {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp)) {
                            // phone code styling
                            Text("+91", color = Color.LightGray, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, null, tint = Color.LightGray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.width(1.dp).height(20.dp).background(Color.LightGray))
                        }
                    }
                } else null,
                placeholder = { Text(label, color = Color.LightGray, fontSize = 14.sp) }
            )
        }
    }
}