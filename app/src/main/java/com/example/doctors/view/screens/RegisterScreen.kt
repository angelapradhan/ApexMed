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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    // state management
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // navigation logic
    LaunchedEffect(authState.registrationSuccessful) {
        if (authState.registrationSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar("Account registered successfully!")
                viewModel.logout()
                delay(500)
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
                }
                viewModel.clearStateFlags()
            }
        }
    }

    // animation
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

                        Text("Create Account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Text("Sign up to get started", fontSize = 14.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(30.dp))

                        // full name
                        CustomInputField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // email
                        CustomInputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.MailOutline,
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // password
                        CustomInputField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            isPasswordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible },
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // confirm password
                        CustomInputField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            isPasswordVisible = confirmPasswordVisible,
                            onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible },
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(35.dp))

                        // register
                        Button(
                            onClick = {
                                if (password == confirmPassword) {
                                    val names = name.trim().split(" ", limit = 2)
                                    viewModel.register(
                                        firstName = names.getOrElse(0) { "" },
                                        lastName = names.getOrElse(1) { "" },
                                        email = email.trim(),
                                        password = password,
                                        contact = ""
                                    )
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
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
                                Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // error message
                        if (authState.error != null) {
                            Text(authState.error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        // log in link
                        Row(modifier = Modifier.padding(vertical = 30.dp)) {
                            Text("Already have an account?", color = Color.Gray)
                            Text(
                                " Log in",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { navController.navigate(Routes.LOGIN) }
                            )
                        }
                    }
                }
            }
        }
    }
}
