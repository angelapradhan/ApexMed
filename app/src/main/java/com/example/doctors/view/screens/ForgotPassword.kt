package com.example.doctors.view.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.model.PasswordResetStep
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    // State
    var currentStep by remember { mutableStateOf(PasswordResetStep.FORGOT_PASSWORD) }
    var emailOrPhone by remember { mutableStateOf("") }
    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle link sent
    LaunchedEffect(authState.resetEmailSent) {
        if (authState.resetEmailSent) {
            currentStep = PasswordResetStep.EMAIL_SENT
            viewModel.clearStateFlags()
        }
    }

    // Handle errors
    LaunchedEffect(authState.error) {
        authState.error?.let {
            scope.launch {
                snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
                viewModel.clearStateFlags()
            }
        }
    }

    // Handle reset success
    LaunchedEffect(authState.passwordResetSuccessful) {
        if (authState.passwordResetSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar(message = "Password reset successfully!", duration = SnackbarDuration.Long)
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                }
                viewModel.clearStateFlags()
            }
        }
    }

    // animation
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
            Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(0.1f), CircleShape))
            Box(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).background(Color.White.copy(0.1f), CircleShape))

            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 54.dp)
                    .clickable {
                        if (currentStep == PasswordResetStep.FORGOT_PASSWORD) {
                            navController.popBackStack()
                        } else {
                            currentStep = when (currentStep) {
                                PasswordResetStep.EMAIL_SENT -> PasswordResetStep.FORGOT_PASSWORD
                                PasswordResetStep.RESET_PASSWORD -> PasswordResetStep.EMAIL_SENT
                                else -> PasswordResetStep.FORGOT_PASSWORD
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (currentStep) {
                        PasswordResetStep.FORGOT_PASSWORD -> "Forgot Password"
                        PasswordResetStep.EMAIL_SENT -> "Email Confirmation"
                        PasswordResetStep.RESET_PASSWORD -> "Reset Password"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            // Main content card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
                    .offset(y = translateY)
                    .alpha(alpha),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                        .background(Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Content steps
                        when (currentStep) {
                            PasswordResetStep.FORGOT_PASSWORD -> ForgotPasswordField(
                                emailOrPhone = emailOrPhone,
                                onValueChange = { emailOrPhone = it },
                                onLoginClick = { viewModel.forgetPassword(emailOrPhone.trim()) },
                                isLoading = authState.isLoading,
                                primaryColor = PrimaryBlue
                            )

                            PasswordResetStep.EMAIL_SENT -> EmailSentConfirmation(
                                onCheckEmailClick = { currentStep = PasswordResetStep.RESET_PASSWORD },
                                primaryColor = PrimaryBlue
                            )

                            PasswordResetStep.RESET_PASSWORD -> ResetPasswordScreen(
                                onResetPasswordClick = { code, newPassword ->
                                    viewModel.confirmPasswordReset(code, newPassword)
                                },
                                isLoading = authState.isLoading,
                                primaryColor = PrimaryBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

// input email/phone
@Composable
fun ForgotPasswordField(
    emailOrPhone: String,
    onValueChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    primaryColor: Color
) {
    Image(
        painter = painterResource(id = R.drawable.img_secure_lock),
        contentDescription = "Forgot Password Illustration",
        modifier = Modifier.size(150.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Provide your username or email, and we'll send you a secure link to confirm your identity.",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(32.dp))

    OutlinedTextField(
        value = emailOrPhone,
        onValueChange = onValueChange,
        label = { Text("Enter email or phone number") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = Color.LightGray
        )
    )
    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading && emailOrPhone.isNotEmpty(),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text("Send Link", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

// confirmation
@Composable
fun EmailSentConfirmation(onCheckEmailClick: () -> Unit, primaryColor: Color) {
    Image(
        painter = painterResource(id = R.drawable.img_secure_lock),
        contentDescription = "Secure Link Illustration",
        modifier = Modifier.size(150.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(text = "Secure Link Sent", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = primaryColor)
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = "Check your email! You can reset your password directly by clicking the link, or copy the confirmation code (oobCode) from the link and paste it here to reset within the app.",
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray,
        lineHeight = 22.sp,
        modifier = Modifier.padding(horizontal = 12.dp)
    )
    Spacer(modifier = Modifier.height(48.dp))
    Button(
        onClick = onCheckEmailClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text("I Have the Code / Reset in App", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}

// password reset form
@Composable
fun ResetPasswordScreen(
    onResetPasswordClick: (code: String, newPassword: String) -> Unit,
    isLoading: Boolean,
    primaryColor: Color
) {
    var resetCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var actualConfirmPasswordVisible by remember { mutableStateOf(false) }

    Image(
        painter = painterResource(id = R.drawable.img_secure_lock),
        contentDescription = "Reset Password Illustration",
        modifier = Modifier.size(150.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Paste the confirmation code from the email link below to reset your password.",
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Code input
    OutlinedTextField(
        value = resetCode,
        onValueChange = { resetCode = it },
        label = { Text("Confirmation Code") },
        leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Code") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Password input
    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it },
        label = { Text("New Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "New Password") },
        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading
    )
    Spacer(modifier = Modifier.height(16.dp))

    // Confirm password input
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        label = { Text("Confirm New Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm New Password") },
        visualTransformation = if (actualConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (actualConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { actualConfirmPasswordVisible = !actualConfirmPasswordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading,
        isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Submit button
    Button(
        onClick = {
            if (newPassword == confirmPassword && resetCode.isNotEmpty()) {
                onResetPasswordClick(resetCode.trim(), newPassword)
            }
        },
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        enabled = !isLoading && newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword == confirmPassword,
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Text("Reset Password", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}