package com.example.doctors.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doctors.model.User
import com.example.doctors.repository.AuthRepository
import com.example.doctors.repository.AuthRepositoryImpl

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


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
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    var state by mutableStateOf(AuthState())
        private set


    fun clearStateFlags() {
        state = state.copy(
            error = null,
            resetEmailSent = false,
            registrationSuccessful = false,
        passwordResetSuccessful = false)
    }

    fun login(email: String, password: String) {
        state = state.copy(isLoading = true, error = null)

        repository.login(email, password) { success, message ->
            if (success) {

                fetchCurrentUser()
            } else {
                state = state.copy(
                    error = message,
                    isLoading = false,
                    isAuthenticated = false
                )
            }
        }
    }


    fun forgetPassword(email: String) {
        state = state.copy(isLoading = true, error = null, resetEmailSent = false)

        repository.forgetPassword(email) { success, message ->
            state = state.copy(isLoading = false)
            if (success) {
                state = state.copy(resetEmailSent = true)
            } else {
                state = state.copy(error = message)
            }
        }
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        contact: String
    ) {
        state = state.copy(isLoading = true, error = null)

        repository.register(email, password) { success, message, userId ->
            if (success && userId.isNotEmpty()) {
                val newUser = User(
                    userId = userId,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    contact = contact,
                    isDoctor = false
                )

                repository.addUserToDatabase(userId, newUser) { dbSuccess, dbMessage ->
                    state = state.copy(isLoading = false)
                    if (dbSuccess) {
                        logout()

                        state = state.copy(
                            registrationSuccessful = true
                        )
                    } else {

                        logout()
                        state = state.copy(error = dbMessage)
                    }
                }
            } else {
                state = state.copy(
                    error = message,
                    isLoading = false
                )
            }
        }
    }

    fun fetchCurrentUser() {
        if (!state.isAuthenticated) {
            state = state.copy(isLoading = true)
        }

        repository.getCurrentUser { success, user ->
            state = state.copy(isLoading = false)
            if (success && user != null) {
                state = state.copy(
                    currentUser = user,
                    isAuthenticated = true
                )
            } else {

                logout()
            }
        }
    }
    fun logout() {
        try {

            FirebaseAuth.getInstance().signOut()

            state = state.copy(
                isAuthenticated = false,
                isLoading = false,
                error = null,
                currentUser = null
            )
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error during logout:", e)
            state = state.copy(
                error = "Logout failed. Please try again.",
                isLoading = false
            )
        }
    }


    fun confirmPasswordReset(code: String, newPassword: String) {
        state = state.copy(isLoading = true, error = null)

        repository.confirmPasswordReset(code, newPassword) { success, message ->
            state = state.copy(
                isLoading = false,
                passwordResetSuccessful = success,
                error = if (success) null else message
            )
        }
    }
}