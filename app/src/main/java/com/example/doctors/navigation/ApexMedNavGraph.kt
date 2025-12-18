package com.example.doctors.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apexmed.ui.screens.SplashScreen
import com.example.doctors.view.screens.DashboardScreen

import com.example.doctors.view.screens.ForgotPasswordScreen
import com.example.doctors.view.screens.LoginScreen
import com.example.doctors.view.screens.OnboardingScreen
import com.example.doctors.view.screens.RegisterScreen


object Routes {
    const val SPLASH = "splash"

    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"
}

@Composable
fun ApexMedNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(navController)
        }
        composable(Routes.ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController)
        }
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController)
        }

    }
}