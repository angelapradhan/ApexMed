package com.example.doctors.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.doctors.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookingViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // List for UI to observe
    var bookedAppointments = mutableStateListOf<Appointment>()
        private set

    init {
        fetchBookings()
    }

    // Load user bookings from Firestore
    fun fetchBookings() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                val list = result.toObjects(Appointment::class.java)
                bookedAppointments.clear()
                bookedAppointments.addAll(list)
            }
    }

    // Save booking and create notification
    fun addBooking(appointment: Appointment) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("bookings")
            .add(appointment)
            .addOnSuccessListener {
                bookedAppointments.add(appointment)

                // Add notification
                val notification = mapOf(
                    "title" to "Booking Confirmed",
                    "message" to "Appointment confirmed with",
                    "doctorName" to appointment.doctorName,
                    "appointmentTime" to appointment.time,
                    "timeAgo" to "Just now",
                    "type" to "Today",
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("users").document(userId).collection("notifications").add(notification)
            }
    }

    // Remove booking from Firestore and list
    fun cancelBooking(appointment: Appointment) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("bookings")
            .whereEqualTo("date", appointment.date)
            .whereEqualTo("time", appointment.time)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    document.reference.delete()
                }
                bookedAppointments.remove(appointment)
            }
    }
}