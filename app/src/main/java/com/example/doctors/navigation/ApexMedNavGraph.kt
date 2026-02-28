package com.example.doctors.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.apexmed.ui.screens.SplashScreen
import com.example.doctors.view.screens.AllBookingsScreen
import com.example.doctors.view.screens.ContactUsScreen
import com.example.doctors.view.screens.DashboardScreen
import com.example.doctors.view.screens.DoctorDetailScreen
import com.example.doctors.view.screens.ForgotPasswordScreen
import com.example.doctors.view.screens.LoginScreen
import com.example.doctors.view.screens.NotificationScreen
import com.example.doctors.view.screens.OnboardingScreen
import com.example.doctors.view.screens.PersonalDetailsScreen
import com.example.doctors.view.screens.ProfileScreen
import com.example.doctors.view.screens.RegisterScreen
import com.example.doctors.view.screens.SecurityScreen

import com.example.doctors.viewmodel.AuthViewModel
import com.example.doctors.viewmodel.UserViewModel
import com.example.doctors.viewmodel.BookingViewModel
import com.example.doctors.viewmodel.NotificationViewModel

// Route for navigation
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"
    const val SEARCH = "search_screen"
    const val DOCTOR_DETAIL = "doctor_detail/{doctorId}"
    const val ALL_BOOKINGS = "all_bookings"
    const val NOTIFICATIONS = "notifications"
}

@Composable
fun ApexMedNavGraph(navController: NavHostController) {
    // Initialize ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val bookingViewModel: BookingViewModel = viewModel()
    val notificationViewModel: NotificationViewModel = viewModel()

    // Setup Navigation Host
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }
        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }

        // Auth Screens
        composable(Routes.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Routes.REGISTER) { RegisterScreen(navController, authViewModel) }
        composable(Routes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }

        // Main Screen
        composable(Routes.DASHBOARD) {
            DashboardScreen(navController, userViewModel, bookingViewModel)
        }

        // Doctor Detail
        composable(
            route = Routes.DOCTOR_DETAIL,
            arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId")
            DoctorDetailScreen(
                navController = navController,
                doctorId = doctorId,
                bookingViewModel = bookingViewModel,
                userViewModel = userViewModel
            )
        }

        // Profile and Settings
        composable(Routes.PROFILE) {
            ProfileScreen(
                navController = navController,
                userViewModel = userViewModel,
                authViewModel = authViewModel
            )
        }

        composable(Routes.ALL_BOOKINGS) {
            AllBookingsScreen(navController = navController, bookingViewModel = bookingViewModel)
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationScreen(navController, notificationViewModel)
        }

        composable("personal_details") {
            PersonalDetailsScreen(navController, userViewModel)
        }

        composable("security_screen") {
            SecurityScreen(navController, userViewModel)
        }

        composable("contact_us") { ContactUsScreen(navController) }
    }
}