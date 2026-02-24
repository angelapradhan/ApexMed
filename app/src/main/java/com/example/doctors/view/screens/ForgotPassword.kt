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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue
import com.example.doctors.viewmodel.AuthViewModel
import kotlinx.coroutines.launch


enum class PasswordResetStep {
    FORGOT_PASSWORD,
    EMAIL_SENT,

    RESET_PASSWORD
}

@Composable
fun ForgotPasswordScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    var currentStep by remember { mutableStateOf(PasswordResetStep.FORGOT_PASSWORD) }
    var emailOrPhone by remember { mutableStateOf("") }

    val authState = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // --- NAVIGATION LOGIC (Original) ---
    LaunchedEffect(authState.resetEmailSent) {
        if (authState.resetEmailSent) {
            currentStep = PasswordResetStep.EMAIL_SENT
            viewModel.clearStateFlags()
        }
    }

    LaunchedEffect(authState.error) {
        if (authState.error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(message = authState.error!!, duration = SnackbarDuration.Short)
                viewModel.clearStateFlags()
            }
        }
    }

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

    // --- ADDED ANIMATION (For consistency with Login) ---
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val alpha by animateFloatAsState(targetValue = if (isVisible) 1f else 0f, animationSpec = tween  (1000), label = "fade")
    val translateY by animateDpAsState(targetValue = if (isVisible) 0.dp else 180.dp, animationSpec = tween(1000), label = "float")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Root Container with Blue Background and Circles
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(PrimaryBlue)
        ) {
            // Decorative Circles
            Box(modifier = Modifier.size(200.dp).offset(x = (-50).dp, y = (-50).dp).background(Color.White.copy(0.1f),
                CircleShape
            ))
            Box(modifier = Modifier.size(150.dp).align(Alignment.TopEnd).offset(x = 40.dp, y = 20.dp).background(Color.White.copy(0.1f), CircleShape))

            // TOP NAVIGATION BAR
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

            // FLOATING CONTENT CARD
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 110.dp)
                    .offset(y = translateY) // Slide animation
                    .alpha(alpha),           // Fade animation
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
                        // Renders your original logic/UI based on step
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

