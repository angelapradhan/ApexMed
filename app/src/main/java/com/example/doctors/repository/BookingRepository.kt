package com.example.doctors.repository

import com.example.doctors.model.Appointment

interface BookingRepository {
    fun addBooking(appointment: Appointment, callback: (Boolean, String) -> Unit)
    fun getBookings(callback: (List<Appointment>) -> Unit)
}