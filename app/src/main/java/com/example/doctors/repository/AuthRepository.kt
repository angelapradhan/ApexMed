package com.example.doctors.repository

import com.example.doctors.model.User

interface AuthRepository {
    fun login(email: String, password: String,
              callback: (Boolean, String)-> Unit)

    fun register(
        email: String, password: String,
        callback: (Boolean, String, String) -> Unit
    )

    fun addUserToDatabase(userId: String, model: User,
                          callback: (Boolean, String) -> Unit)

    fun forgetPassword(email:String,callback: (Boolean, String) -> Unit)

    fun getCurrentUser(callback: (Boolean, User?) -> Unit)

    fun confirmPasswordReset(code: String, newPassword: String, callback: (Boolean, String?) -> Unit)
}