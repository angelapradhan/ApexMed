package com.example.doctors.repository

import com.example.doctors.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BookingRepositoryImpl : BookingRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // 'Bookings' nam ko node ma data save hunchha
    private val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("Bookings")

    override fun addBooking(
        appointment: Appointment,
        callback: (Boolean, String) -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return callback(false, "User not logged in")

        // Naya unique booking ID generate garne
        val bookingId = ref.push().key ?: return callback(false, "Failed to generate ID")

        // Appointment object ma user ID set garne
        val bookingData = appointment.copy(userId = userId, bookingId = bookingId)

        ref.child(bookingId).setValue(bookingData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Booking successful!")
                } else {
                    callback(false, task.exception?.message ?: "Booking failed")
                }
            }
    }

    override fun getBookings(callback: (List<Appointment>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return callback(emptyList())

        // Login bhayeko user ko matra bookings lyaune
        ref.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookingList = mutableListOf<Appointment>()
                    for (bookingSnapshot in snapshot.children) {
                        val booking = bookingSnapshot.getValue(Appointment::class.java)
                        if (booking != null) {
                            bookingList.add(booking)
                        }
                    }
                    callback(bookingList)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
}