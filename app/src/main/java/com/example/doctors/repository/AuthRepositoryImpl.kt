package com.example.doctors.repository

import com.example.doctors.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthRepositoryImpl : AuthRepository{
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Users")
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

    override fun addUserToDatabase(
        userId: String,
        model: User,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "User registered successfully in database")
            } else {
                callback(false, task.exception?.message ?: "Database write failed")
            }
        }
    }

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

    override fun getCurrentUser(callback: (Boolean, User?) -> Unit) {
        val userId = auth.currentUser?.uid

        if (userId == null) {
            callback(false, null)
            return
        }


        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    callback(true, user)
                } else {
                    callback(false, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, null)
            }
        })
    }


    override fun confirmPasswordReset(
        code: String,
        newPassword: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.confirmPasswordReset(code, newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // SUCCESS: Notify the ViewModel
                    callback(true, "Password reset successful.")
                } else {
                    val errorMessage = task.exception?.message ?: "Failed to reset password (Unknown Error)."
                    callback(false, errorMessage)
                }
            }
    }
}