package com.example.doctors.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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

    LaunchedEffect(authState.resetEmailSent) {
        if (authState.resetEmailSent) {
            currentStep = PasswordResetStep.EMAIL_SENT
            viewModel.clearStateFlags()
        }
    }

    LaunchedEffect(authState.error) {
        if (authState.error != null) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = authState.error!!,
                    duration = SnackbarDuration.Short
                )
                viewModel.clearStateFlags()
            }
        }
    }

    LaunchedEffect(authState.passwordResetSuccessful) {
        if (authState.passwordResetSuccessful) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = "Password reset successfully!",
                    duration = SnackbarDuration.Long
                )

                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.FORGOT_PASSWORD) { inclusive = true }
                }
                viewModel.clearStateFlags()
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryBlue)
                .padding(top = 24.dp)
                .padding(paddingValues),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentStep == PasswordResetStep.FORGOT_PASSWORD) {
                        navController.popBackStack()
                    } else {

                        currentStep = when (currentStep) {
                            PasswordResetStep.EMAIL_SENT -> PasswordResetStep.FORGOT_PASSWORD
                            PasswordResetStep.RESET_PASSWORD -> PasswordResetStep.EMAIL_SENT
                            else -> PasswordResetStep.FORGOT_PASSWORD
                        }
                    }
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
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
            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (currentStep) {
                    PasswordResetStep.FORGOT_PASSWORD -> ForgotPasswordField(
                        emailOrPhone = emailOrPhone,
                        onValueChange = { emailOrPhone = it },
                        onLoginClick = {
                            viewModel.forgetPassword(emailOrPhone.trim())
                        },
                        isLoading = authState.isLoading,
                        primaryColor = PrimaryBlue
                    )

                    PasswordResetStep.EMAIL_SENT -> EmailSentConfirmation(
                        onCheckEmailClick = {
                            currentStep = PasswordResetStep.RESET_PASSWORD
                        },
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

// forget password
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
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
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

//email
@Composable
fun EmailSentConfirmation(onCheckEmailClick: () -> Unit, primaryColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.9f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.MailOutline,
                contentDescription = "Mail Icon",
                tint = Color.White,
                modifier = Modifier.size(48.dp).padding(bottom = 8.dp)
            )
            Text(
                text = "Secure Link Sent",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "We have sent a secure link to your email. Click it and retrieve the long code before proceeding to reset your password in the app.",
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
    Spacer(modifier = Modifier.height(180.dp))

    Button(
        onClick = onCheckEmailClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
    ) {
        Text("I Have the Code / Reset Now", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}


//reset password
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
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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

    // reset code
    OutlinedTextField(
        value = resetCode,
        onValueChange = { resetCode = it },
        label = { Text("Confirmation Code (Paste the long oobCode)") },
        leadingIcon = { Icon(Icons.Default.MailOutline, contentDescription = "Code") },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        enabled = !isLoading
    )
    Spacer(modifier = Modifier.height(16.dp))

    // new ps
    OutlinedTextField(
        value = newPassword,
        onValueChange = { newPassword = it },
        label = { Text("New Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "New Password") },
        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (newPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading
    )
    Spacer(modifier = Modifier.height(16.dp))

    // confirm ps
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = { confirmPassword = it },
        label = { Text("Confirm New Password") },
        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Confirm New Password") },
        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading,
        isError = newPassword != confirmPassword && confirmPassword.isNotEmpty()
    )
    Spacer(modifier = Modifier.height(32.dp))

    // Reset Password Button
    Button(
        onClick = {
            if (newPassword == confirmPassword && resetCode.isNotEmpty()) {
                onResetPasswordClick(resetCode.trim(), newPassword)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
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