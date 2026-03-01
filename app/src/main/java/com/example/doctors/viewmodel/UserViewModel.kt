package com.example.doctors.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.doctors.model.User
import com.example.doctors.repository.UserRepository
import com.example.doctors.repository.UserRepositoryImpl
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

// UI State for user profile
data class UserState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserViewModel(
    private val userRepository: UserRepository = UserRepositoryImpl()
) : ViewModel() {
    private val realtimeDb = com.google.firebase.database.FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    // Observable UI state
    var state by mutableStateOf(UserState())
        private set

    init {
        fetchCurrentUser()
    }

    // Load profile data
    fun fetchCurrentUser() {
        state = state.copy(isLoading = true)
        userRepository.getCurrentUser { success, user ->
            if (success && user != null) {
                state = state.copy(currentUser = user, isLoading = false)
            } else {
                state = state.copy(isLoading = false, error = "Failed to fetch user")
            }
        }
    }

    // Update profile text fields
    fun updateUserDetails(name: String, userName: String, phoneNumber: String, context: Context) {
        val uid = auth.currentUser?.uid ?: return
        val nameParts = name.trim().split("\\s+".toRegex())
        val fName = nameParts.getOrNull(0) ?: ""
        val lName = nameParts.drop(1).joinToString(" ")

        val updates = mapOf("firstName" to fName, "lastName" to lName, "userName" to userName, "contact" to phoneNumber)

        realtimeDb.child(uid).updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                fetchCurrentUser()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Update Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Re-auth and update password
    fun updatePassword(currentPass: String, newPass: String, context: Context) {
        val user = auth.currentUser ?: return
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPass)

        user.reauthenticate(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        realtimeDb.child("Users").child(user.uid).child("password").setValue(newPass)
                        Toast.makeText(context, "Password Updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Current password incorrect!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete account and sign out
    fun deleteAccount(context: Context, onSuccess: () -> Unit) {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                auth.signOut()
                state = UserState()
                onSuccess()
            } else {
                Toast.makeText(context, "Delete failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}