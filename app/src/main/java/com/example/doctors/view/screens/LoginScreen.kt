package com.example.doctors.view.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // dashboard navigation
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.clearStateFlags()
        }
    }

    // entrance animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween(1000), label = "")
    val translateY by animateDpAsState(targetValue = if (isVisible) 0.dp else 80.dp, animationSpec = tween(1000), label = "")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {

            // background image and logo
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // back button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(start = 12.dp, top = 40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }

                // logo
                Image(
                    painter = painterResource(id = R.drawable.apexmed_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 65.dp)
                        .size(130.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // white card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 265.dp)
                    .offset(y = translateY)
                    .alpha(alpha)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(35.dp))

                        Text("Welcome Back", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Sign in to continue", fontSize = 14.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(30.dp))

                        // Email Field
                        CustomInputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.MailOutline,
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        CustomInputField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            isPasswordVisible = isPasswordVisible,
                            onTogglePassword = { isPasswordVisible = !isPasswordVisible },
                            enabled = !authState.isLoading
                        )

                        // forget password
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                "Forgot Password?",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { navController.navigate(Routes.FORGOT_PASSWORD) }
                            )
                        }

                        Spacer(modifier = Modifier.height(35.dp))

                        // sign in button
                        Button(
                            onClick = {
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    viewModel.login(email.trim(), password)
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Fields cannot be empty") }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            enabled = !authState.isLoading
                        ) {
                            if (authState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Sign In", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // error mssg
                        if (authState.error != null) {
                            Text(authState.error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))


                        // sign up link
                        Row(modifier = Modifier.padding(vertical = 30.dp)) {
                            Text("Don't have an account?", color = Color.Gray)
                            Text(
                                " Sign up",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navController.navigate(Routes.REGISTER) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryBlue) },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = onTogglePassword!!) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        } else null,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedLabelColor = PrimaryBlue
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}