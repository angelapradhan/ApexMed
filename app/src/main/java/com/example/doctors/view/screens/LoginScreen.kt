package com.example.doctors.view.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
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

    // --- ANIMATION STATE ---
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween(1000), label = "fade")
    val translateY by animateDpAsState(targetValue = if (isVisible) 0.dp else 180.dp, animationSpec = tween(1000), label = "float")

    Box(modifier = Modifier.fillMaxSize().background(PrimaryBlue)) {
        // Decorative Shapes
        Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(0.1f), CircleShape))
        Box(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).background(Color.White.copy(0.1f), CircleShape))

        // Static Back Button
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 54.dp).clickable { navController.popBackStack() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Back", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
        Image(painter = painterResource(id = R.drawable.img_google_logo), contentDescription = "Google", modifier = Modifier.size(24.dp))
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