package com.example.doctors.repository

import com.example.doctors.model.User

interface AuthRepository {
    fun login(email: String, password: String, callback: (Boolean, String) -> Unit)
    fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit)
    fun forgetPassword(email: String, callback: (Boolean, String) -> Unit)
    fun logout()
    fun confirmPasswordReset(code: String, newPassword: String, callback: (Boolean, String?) -> Unit)
}