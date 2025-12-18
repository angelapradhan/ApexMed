package com.apexmed.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.Animatable
import androidx.navigation.NavHostController

import com.example.doctors.R
import com.example.doctors.navigation.Routes
import com.example.doctors.ui.theme.PrimaryBlue // Not strictly used for the dot now, but good to keep
import com.example.doctors.ui.theme.SplashEndWhite
import com.example.doctors.ui.theme.SplashStartBlue // Used for dot and text color
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(navController: NavHostController) {

    val LogoStartTime = 200L
    val TextStartTime = 600L
    val TextDelayBeforeStart = TextStartTime - LogoStartTime
    val HoldTime = 1200L

    val InitialLightBlue = PrimaryBlue
    val FinalBackgroundColor = SplashEndWhite

    val finalLogoScale = remember { Animatable(0.0f) }
    val finalLogoAlpha = remember { Animatable(0.0f) }
    val logoTextAlpha = remember { Animatable(0.0f) }
    val animatedBackgroundColor = remember { Animatable(InitialLightBlue) }
    val dotScale = remember { Animatable(3.0f) }

    LaunchedEffect(Unit) {

        // bg color
        launch {
            animatedBackgroundColor.animateTo(
                targetValue = FinalBackgroundColor,
                animationSpec = tween(durationMillis = 400, easing = LinearEasing)
            )
        }

        // blue dot animation
        launch {
            delay(50)
            dotScale.animateTo(
                targetValue = 0.0f,
                animationSpec = tween(
                    durationMillis = 700,
                    easing = FastOutSlowInEasing
                )
            )
        }


        delay(LogoStartTime)
        launch {
            finalLogoScale.animateTo(
                targetValue = 1.0f,

                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
            finalLogoAlpha.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 300)
            )
        }

        // logp
        delay(TextDelayBeforeStart)
        logoTextAlpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 400)
        )

        // hold and navigate to login
        delay(HoldTime)

        navController.popBackStack()
        navController.navigate(Routes.ONBOARDING)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(animatedBackgroundColor.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Box(
            modifier = Modifier
                .size(100 .dp)
                .scale(dotScale.value)
                .background(PrimaryBlue, CircleShape)
        )


        Image(
            painter = painterResource(id = R.drawable.apexmed_logo),
            contentDescription = "ApexMed Logo",
            modifier = Modifier
                .size(160.dp) // Adjusted size
                .scale(finalLogoScale.value)
                .alpha(finalLogoAlpha.value)
                .offset(y = (-70).dp)
        )


        //app name
        Text(
            text = "ApexMed",
            color = PrimaryBlue,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(logoTextAlpha.value)
                .offset(y = (-70).dp)
        )

        // tagline
        Text(
            text = "Your Appointment Hub",
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.alpha(logoTextAlpha.value)
                .offset(y = (-70).dp)
        )
    }
}