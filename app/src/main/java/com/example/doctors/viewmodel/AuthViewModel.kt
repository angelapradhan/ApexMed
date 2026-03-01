package com.example.doctors.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.doctors.model.User
import com.example.doctors.repository.AuthRepository
import com.example.doctors.repository.AuthRepositoryImpl
import com.example.doctors.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth

// UI State for Authentication
data class AuthState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = (FirebaseAuth.getInstance().currentUser != null),
    val resetEmailSent: Boolean = false,
    val passwordResetSuccessful: Boolean = false,
    val registrationSuccessful: Boolean = false
)

class AuthViewModel(

    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {
    private val userRepository = UserRepositoryImpl()
    private val auth = FirebaseAuth.getInstance()

    // Observable UI state
    var state by mutableStateOf(AuthState())
        private set

    // Log in user
    fun login(email: String, password: String) {
        state = state.copy(isLoading = true, error = null)
        authRepository.login(email, password) { success, message ->
            if (success) {
                state = state.copy(isLoading = false, isAuthenticated = true)
            } else {
                state = state.copy(error = message, isLoading = false, isAuthenticated = false)
            }
        }
    }

    // Register & save to DB
    fun register(firstName: String, lastName: String, email: String, password: String, contact: String) {
        state = state.copy(isLoading = true, error = null)

        authRepository.register(email, password) { success, message, userId ->
            if (success && userId.isNotEmpty()) {
                val newUser = User(userId = userId, email = email, firstName = firstName, lastName = lastName, contact = contact, isDoctor = false)

                userRepository.addUserToDatabase(userId, newUser) { dbSuccess, dbMessage ->
                    state = state.copy(isLoading = false)
                    if (dbSuccess) {
                        logout()
                        state = state.copy(registrationSuccessful = true)
                    } else {
                        state = state.copy(error = dbMessage)
                    }
                }
            } else {
                state = state.copy(error = message, isLoading = false)
            }
        }
    }

    // Request reset link
    fun forgetPassword(email: String) {
        state = state.copy(isLoading = true, error = null, resetEmailSent = false)

        authRepository.forgetPassword(email) { success, message ->
            state = state.copy(isLoading = false)
            if (success) state = state.copy(resetEmailSent = true)
            else state = state.copy(error = message)
        }
    }

    // Verify & update password
    fun confirmPasswordReset(code: String, newPassword: String) {
        state = state.copy(isLoading = true, error = null)

        authRepository.confirmPasswordReset(code, newPassword) { success, message ->
            state = state.copy(isLoading = false, passwordResetSuccessful = success, error = if (success) null else message)
        }
    }

    // Sign out & reset state
    fun logout() {
        try {
            auth.signOut()
            state = AuthState(isAuthenticated = false)
        } catch (e: Exception) {
            state = state.copy(error = "Logout failed.")
        }
    }

    // Reset UI flags
    fun clearStateFlags() {
        state = state.copy(error = null, resetEmailSent = false, registrationSuccessful = false, passwordResetSuccessful = false)
    }
}