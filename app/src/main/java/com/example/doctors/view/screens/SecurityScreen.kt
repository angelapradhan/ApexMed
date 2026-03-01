package com.example.doctors.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctors.viewmodel.UserViewModel


@Composable
fun SecurityScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    // state
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    // gradient brush
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
                        Text("Update Password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                // form
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("Please fill the form below to update password", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(24.dp))

                            SecurityInputField(label = "Current password", value = currentPassword) { currentPassword = it }
                            Spacer(modifier = Modifier.height(16.dp))

                            SecurityInputField(label = "New Password", value = newPassword) { newPassword = it }

                            // requirements
                            PasswordRequirementsUI(newPassword)

                            Spacer(modifier = Modifier.height(16.dp))
                            SecurityInputField(label = "Confirm password", value = confirmPassword) { confirmPassword = it }

                            Spacer(modifier = Modifier.height(32.dp))

                            // update button
                            Button(
                                onClick = {
                                    // check reqs
                                    val hasNumber = newPassword.any { it.isDigit() }
                                    val hasSpecial = newPassword.any { !it.isLetterOrDigit() }
                                    val hasUpper = newPassword.any { it.isUpperCase() }

                                    if (newPassword != confirmPassword) {
                                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                                    } else if (newPassword.length < 8) {
                                        Toast.makeText(context, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show()
                                    } else if (!hasNumber) {
                                        Toast.makeText(context, "Please include at least one number!", Toast.LENGTH_SHORT).show()
                                    } else if (!hasSpecial) {
                                        Toast.makeText(context, "Please include at least one special character!", Toast.LENGTH_SHORT).show()
                                    } else if (!hasUpper) {
                                        Toast.makeText(context, "Please include at least one uppercase letter!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // update data
                                        userViewModel.updatePassword(currentPassword, newPassword, context)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                            ) {
                                Text("Update Password", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
fun SecurityInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    // visibility
    var passwordVisible by remember { mutableStateOf(false) }

    Column {
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            // hide text
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF7F8F9),
                unfocusedContainerColor = Color(0xFFF7F8F9),
                focusedTextColor = Color.Gray,
                unfocusedTextColor = Color.Gray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = if (passwordVisible) LoginBlue else Color.LightGray
                    )
                }
            }
        )
    }
}

@Composable
fun PasswordRequirementsUI(password: String) {
    // req checks
    val checks = listOf(
        "A minimum of 8 characters" to (password.length >= 8),
        "At least one number" to password.any { it.isDigit() },
        "At least 1 special character" to password.any { !it.isLetterOrDigit() },
        "At least one uppercase letter" to password.any { it.isUpperCase() }
    )

    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text("Your password must contains:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        checks.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                row.forEach { (text, isMet) ->
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            if (isMet) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isMet) Color(0xFF4CAF50) else Color(0xFFE57373)
                        )
                        Text(text, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}