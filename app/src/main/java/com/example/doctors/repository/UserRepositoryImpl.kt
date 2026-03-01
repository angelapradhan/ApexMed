package com.example.doctors.repository

import android.net.Uri
import com.example.doctors.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UserRepositoryImpl : UserRepository{
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    override fun addUserToDatabase(
        userId: String,
        model: User,
        callback: (Boolean, String) -> Unit
    ) {
        // Saves new user to database
        ref.child(userId).setValue(model).addOnCompleteListener { task ->
            if (task.isSuccessful) callback(true, "User added to database")
            else callback(false, task.exception?.message ?: "Database write failed")
        }
    }

    override fun getCurrentUser(callback: (Boolean, User?) -> Unit) {
        // Fetches logged in user data
        val userId = auth.currentUser?.uid ?: return callback(false, null)
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                callback(user != null, user)
            }
            override fun onCancelled(error: DatabaseError) { callback(false, null) }
        })
    }

    override fun updateUserDetails(
        userId: String,
        name: String,
        username: String,
        phone: String,
        callback: (Boolean, String) -> Unit
    ) {
        // Updates user profile info
        val updates = mapOf(
            "firstName" to name.substringBefore(" "),
            "lastName" to name.substringAfter(" ", ""),
            "userName" to username,
            "contact" to phone
        )

        ref.child(userId).updateChildren(updates)
            .addOnSuccessListener {
                callback(true, "Details updated successfully!")
            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Failed to update details")
            }
    }

    override fun uploadProfileImage(
        imageUri: Uri,
        callback: (Boolean, String?) -> Unit
    ) {
        // Uploads image to Storage and updates URL in Database
        val userId = auth.currentUser?.uid ?: return callback(false, "User not logged in")
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    updateProfileImageUrl(userId, downloadUrl) { success, _ ->
                        if (success) callback(true, downloadUrl)
                        else callback(false, "Database update failed")
                    }
                }
            }
            .addOnFailureListener { callback(false, it.localizedMessage) }
    }
    // Helper to update URL in DB
    private fun updateProfileImageUrl(userId: String, imageUrl: String, callback: (Boolean, String) -> Unit) {
        ref.child(userId).child("profileImageUrl").setValue(imageUrl)
            .addOnSuccessListener { callback(true, "Database updated!") }
            .addOnFailureListener { callback(false, it.message ?: "Failed") }
    }

    override fun deleteAccount(callback: (Boolean, String) -> Unit) {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.child(user.uid).removeValue()
                callback(true, "Account deleted")
            } else callback(false, task.exception?.message ?: "Failed to delete")
        }
    }
}