package com.example.doctors.repository

import com.example.doctors.model.User

interface UserRepository {
    fun addUserToDatabase(userId: String, model: User, callback: (Boolean, String) -> Unit)
    fun getCurrentUser(callback: (Boolean, User?) -> Unit)
    fun updateUserDetails(userId: String, name: String, username: String, phone: String, callback: (Boolean, String) -> Unit)
    fun uploadProfileImage(imageUri: android.net.Uri, callback: (Boolean, String?) -> Unit)
    fun deleteAccount(callback: (Boolean, String) -> Unit)
}