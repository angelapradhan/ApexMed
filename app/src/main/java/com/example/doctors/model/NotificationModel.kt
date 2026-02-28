package com.example.doctors.model

data class NotificationModel(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val timeAgo: String = "",
    val doctorName: String = "",
    val appointmentTime: String = "",
    val type: String = "Today",
    val timestamp: Long = 0L
)
