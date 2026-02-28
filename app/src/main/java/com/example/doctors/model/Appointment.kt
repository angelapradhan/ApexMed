package com.example.doctors.model

data class Appointment(
    val userId: String = "",
    val bookingId: String = "",

    val doctorName: String = "",
    val doctorType: String = "",
    val imageRes: Int = 0,
    val date: String = "",
    val time: String = "",
    val reason: String = "",
    val type: String = ""
)
