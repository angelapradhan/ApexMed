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
    // --- STATE MANAGEMENT ---
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- NAVIGATION LOGIC ---
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            navController.navigate(Routes.DASHBOARD) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.clearStateFlags()
        }
    }

    // --- ANIMATION SETUP ---
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000), label = "fade"
    )
    val translateY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 180.dp,
        animationSpec = tween(1000), label = "float"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Root Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PrimaryBlue)
        ) {
            // Decorative Background Shapes
            Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(0.1f), CircleShape))
            Box(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).background(Color.White.copy(0.1f), CircleShape))

            // 1. BACK BUTTON (Functional)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 54.dp)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            // 2. ANIMATED CONTENT
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
                    .offset(y = translateY)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // LOGO
                Image(
                    painter = painterResource(id = R.drawable.whitelogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(90.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // WHITE CARD
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Text(
                            text = "Welcome back",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.height(28.dp))

                        // EMAIL FIELD
                        CustomInputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.MailOutline,
                            enabled = !authState.isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // PASSWORD FIELD
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

                        // FORGOT PASSWORD (Fixed Logic)
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                            Text(
                                text = "Forgot password?",
                                color = PrimaryBlue,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    navController.navigate(Routes.FORGOT_PASSWORD)
                                }
                            )
                        }

                        // ERROR TEXT
                        if (authState.error != null) {
                            Text(
                                text = authState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // LOGIN BUTTON (With Loading State)
                        Button(
                            onClick = {
                                if (email.isBlank() || password.isBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please enter all fields")
                                    }
                                } else {
                                    viewModel.login(email.trim(), password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !authState.isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            if (authState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Sign in", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        GoogleSignInButton(onClick = { /* Handle Google Login */ })
                        Spacer(modifier = Modifier.height(30.dp))

                        // SIGN UP LINK (Fixed Logic)
                        Row(modifier = Modifier.padding(bottom = 24.dp)) {
                            Text("Don't have an account?", color = Color.Gray)
                            Text(
                                text = " Sign up",
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable {
                                    navController.navigate(Routes.REGISTER)
                                }
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
            focusedLabelColor = PrimaryBlue,
            unfocusedLabelColor = Color.Gray
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
        Spacer(modifier = Modifier.width(10.dp))
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