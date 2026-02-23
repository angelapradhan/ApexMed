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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    // --- STATE MANAGEMENT (Contact Removed) ---
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- NAVIGATION LOGIC ---
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

    // --- ANIMATIONS ---
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween(1000), label = "fade")
    val translateY by animateDpAsState(targetValue = if (isVisible) 0.dp else 180.dp, animationSpec = tween(1000), label = "float")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PrimaryBlue)
        ) {
            // Decorative Background
            Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(0.1f), CircleShape))
            Box(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).background(Color.White.copy(0.1f), CircleShape))

            // BACK BUTTON
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 54.dp)
                    .clickable { navController.popBackStack() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Back", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
                    .offset(y = translateY)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.whitelogo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(80.dp)
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
                        Text(text = "Create Account", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(28.dp))

                        // FULL NAME
                        CustomInputField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Full Name",
                            icon = Icons.Default.Person,
                            enabled = !authState.isLoading
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // EMAIL
                        CustomInputField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email Address",
                            icon = Icons.Default.MailOutline,
                            enabled = !authState.isLoading
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // PASSWORD
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

                        // CONFIRM PASSWORD
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

                        if (authState.error != null) {
                            Text(text = authState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // REGISTER BUTTON
                        Button(
                            onClick = {
                                if (password == confirmPassword) {
                                    val names = name.trim().split(" ", limit = 2)
                                    viewModel.register(
                                        firstName = names.getOrElse(0) { "" },
                                        lastName = names.getOrElse(1) { "" },
                                        email = email.trim(),
                                        password = password,
                                        contact = "" // Passing empty as you don't want it added
                                    )
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar("Passwords do not match") }
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
                                Text("Register", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        GoogleSignInButton(onClick = { /* Handle Google Sign Up */ })
                        Spacer(modifier = Modifier.height(30.dp))

                        // ALREADY HAVE ACCOUNT LINK
                        Row(modifier = Modifier.padding(bottom = 32.dp)) {
                            Text("Already have an account?", color = Color.Gray)
                            Text(
                                text = " Log in",
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
//    navController: NavHostController,
//    viewModel: AuthViewModel = viewModel()
//) {
//    var name by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//    var contact by remember { mutableStateOf("") }
//
//    // password validation
//    var passwordVisible by remember { mutableStateOf(false) }
//    var confirmPasswordVisible by remember { mutableStateOf(false) }
//
//    val authState = viewModel.state
//    val snackbarHostState = remember { SnackbarHostState() }
//    val scope = rememberCoroutineScope()
//
//    // message
//    LaunchedEffect(authState.registrationSuccessful) {
//        if (authState.registrationSuccessful) {
//            scope.launch {
//                snackbarHostState.showSnackbar(
//                    message = "Account registered successfully!",
//                    duration = SnackbarDuration.Short
//                )
//                viewModel.logout()
//
//                delay(500)
//
//
//                navController.navigate(Routes.LOGIN) {
//                    popUpTo(Routes.REGISTER) { inclusive = true }
//                }
//
//                viewModel.clearStateFlags()
//            }
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
//                .background(Color.White)
//                .padding(paddingValues)
//                .padding(horizontal = 24.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Spacer(modifier = Modifier.height(48.dp))
//            Text(
//                text = "Create ApexMed Account",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                color = PrimaryBlue
//            )
//            Spacer(modifier = Modifier.height(32.dp))
//
//            // name
//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Full Name") },
//                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // contact
//            OutlinedTextField(
//                value = contact,
//                onValueChange = { contact = it },
//                label = { Text("Contact Number") },
//                leadingIcon = { Icon(Icons.Default.Call, contentDescription = "Contact") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // email
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Email Address") },
//                leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Email") },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
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
//
//                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//
//                trailingIcon = {
//                    val image = if (passwordVisible)
//                        Icons.Filled.Visibility
//                    else Icons.Filled.VisibilityOff
//
//                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
//                        Icon(imageVector  = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // confirm password
//            OutlinedTextField(
//                value = confirmPassword,
//                onValueChange = { confirmPassword = it },
//                label = { Text("Confirm Password") },
//                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm Password") },
//
//                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
//
//                trailingIcon = {
//                    val image = if (confirmPasswordVisible)
//                        Icons.Filled.Visibility
//                    else Icons.Filled.VisibilityOff
//
//                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
//                        Icon(imageVector  = image, contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                shape = RoundedCornerShape(8.dp),
//                enabled = !authState.isLoading
//            )
//            Spacer(modifier = Modifier.height(32.dp))
//
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
//            // sign up
//            Button(
//                onClick = {
//                    if (password == confirmPassword) {
//                        val names = name.trim().split(" ", limit = 2)
//                        val firstName = names.getOrElse(0) { "" }
//                        val lastName = names.getOrElse(1) { "" }
//
//                        viewModel.register(
//                            firstName = firstName,
//                            lastName = lastName,
//                            email = email.trim(),
//                            password = password,
//                            contact = contact.trim()
//                        )
//                    } else {
//                        scope.launch {
//                            snackbarHostState.showSnackbar(
//                                message = "Passwords do not match.",
//                                duration = SnackbarDuration.Short
//                            )
//                        }
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
//                    Text("Register", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // or
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
//            // google sign in
//            GoogleLogInButton(onClick = { /* google sign in ko code */ })
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            // login link
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Text("Already have an account?")
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(
//                    text = "Log in",
//                    color = PrimaryBlue,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.clickable { navController.navigate(Routes.LOGIN) }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun GoogleLogInButton(onClick: () -> Unit) {
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