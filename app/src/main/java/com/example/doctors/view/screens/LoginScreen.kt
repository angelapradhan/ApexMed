package com.example.doctors.view.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
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

    // --- DASHBOARD NAVIGATION LOGIC (FIXED) ---
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Routes.DASHBOARD) {
                // Login screen lai backstack bata hataune taki user back garda feri login ma na-aayos
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.clearStateFlags()
        }
    }

    // Entrance Animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween(1000), label = "")
    val translateY by animateDpAsState(targetValue = if (isVisible) 0.dp else 80.dp, animationSpec = tween(1000), label = "")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {

            // 1. BACKGROUND IMAGE & LOGO
            Box(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Back Button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(start = 12.dp, top = 40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }

                // LOGO - On Top of Image
                Image(
                    painter = painterResource(id = R.drawable.apexmed_logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 65.dp)
                        .size(90.dp),
                    contentScale = ContentScale.Fit
                )
            }

            // 2. WHITE CARD CONTENT
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

                        // FORGOT PASSWORD
                        Box(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                "Forgot Password?",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { navController.navigate(Routes.FORGOT_PASSWORD) }
                            )
                        }

                        Spacer(modifier = Modifier.height(35.dp))

                        // SIGN IN BUTTON
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

                        // ERROR MESSAGE (Optional display)
                        if (authState.error != null) {
                            Text(authState.error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        GoogleSignInButton(onClick = { /* Google Login Logic */ })

                        // Sign Up Link
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

// Reusable Components
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

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_google_logo),
            contentDescription = "Google",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text("Continue with Google", color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
    }
}

//
//@Composable
//fun LoginScreen(
//    navController: NavHostController,
//    viewModel: AuthViewModel = viewModel()
//) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var isPasswordVisible by remember { mutableStateOf(false) }
//
//    val authState = viewModel.state
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    // navigation
//    LaunchedEffect(authState.isAuthenticated) {
//        if (authState.isAuthenticated) {
//            navController.navigate(Routes.DASHBOARD) {
//                popUpTo(Routes.LOGIN) { inclusive = true }
//            }
//            viewModel.clearStateFlags()
//        }
//    }
//
//    Scaffold(
//        snackbarHost = { SnackbarHost(snackbarHostState) },
//        containerColor = Color.White
//    ) { paddingValues ->
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.Start
//        ) {
//            Spacer(modifier = Modifier.height(35.dp))
//
//            // logo and name
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Image(
//                    painter = painterResource(id = R.drawable.apexmed_logo),
//                    contentDescription = "ApexMed Logo",
//                    modifier = Modifier.size(60.dp)
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text(
//                    text = "ApexMed",
//                    fontSize = 22.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = PrimaryBlue
//                )
//            }
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // header
//            Text(
//                text = "Log In",
//                fontSize = 28.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Welcome back! Please enter your details.",
//                fontSize = 16.sp,
//                color = Color.Gray
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // email
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email Address") },
//                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Email") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(10.dp),
//                singleLine = true,
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // password
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Password") },
//                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
//                trailingIcon = {
//                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
//                        Icon(
//                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                            contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
//                        )
//                    }
//                },
//                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(10.dp),
//                singleLine = true,
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // forget password
//            Text(
//                text = "Forgot Password?",
//                color = PrimaryBlue,
//                modifier = Modifier
//                    .align(Alignment.End)
//                    .clickable { navController.navigate(Routes.FORGOT_PASSWORD) },
//                fontSize = 14.sp
//            )
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // error display garne
//            if (authState.error != null) {
//                Text(
//                    text = authState.error!!,
//                    color = MaterialTheme.colorScheme.error,
//                    fontSize = 14.sp,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//                LaunchedEffect(authState.error) {
//                    viewModel.clearStateFlags()
//                }
//            }
//
//
//            // login button
//            Button(
//                onClick = {
//                    if (email.isBlank() || password.isBlank()) {
//                        scope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Please enter both email and password.",
//                                duration = SnackbarDuration.Short
//                            )
//                        }
//                    } else {
//                        viewModel.login(email.trim(), password)
//                    }
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp),
//                shape = RoundedCornerShape(12.dp),
//                enabled = !authState.isLoading,
//                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
//            ) {
//                if (authState.isLoading) {
//                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
//                } else {
//                    Text("Login", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // divider and google login
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
//                Text(" OR ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
//                Divider(modifier = Modifier.weight(1f), color = Color.LightGray)
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//
//            GoogleSignInButton(onClick = { /* Google Sign in ko code */ })
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // sign up link
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text("Don't have an account?")
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "Sign up",
//                    color = PrimaryBlue,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.clickable { navController.navigate(Routes.REGISTER) }
//                )
//            }
//        }
//    }
//}
//
//// google sign in button
//@Composable
//fun GoogleSignInButton(onClick: () -> Unit) {
//    OutlinedButton(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(56.dp),
//        shape = RoundedCornerShape(10.dp),
//        border = BorderStroke(1.dp, Color.LightGray),
//        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.DarkGray)
//    ) {
//        Image(
//            painter = painterResource(id = R.drawable.img_google_logo),
//            contentDescription = "Google Logo",
//            modifier = Modifier.size(45.dp)
//        )
//        Spacer(modifier = Modifier.width(10.dp))
//        Text("Continue with Google", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
//    }
//}