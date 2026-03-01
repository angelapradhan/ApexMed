package com.example.doctors.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AuthRepositoryImpl : AuthRepository {
    // Initialize Firebase services
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Users")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Sign in
    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login success")
                } else {
                    callback(false, task.exception?.message ?: "Login failed")
                }
            }
    }

    // Create a new user account
    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    callback(true, "Registration successful", userId)
                } else {
                    callback(false, task.exception?.message ?: "Registration failed", "")
                }
            }
    }

    // Send password reset link to user's email
    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset email sent successfully.")
                } else {
                    val errorMessage = task.exception?.message ?: "Failed to send reset email."
                    callback(false, errorMessage)
                }
            }
    }

    // Sign out
    override fun logout() {
        auth.signOut()
    }

    // Verify reset code and update to new password
    override fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Password reset successful.")
                } else {
                    val errorMessage =
                        task.exception?.message ?: "Failed to reset password (Unknown Error)."
                    callback(false, errorMessage)
                }
            }
    }
}