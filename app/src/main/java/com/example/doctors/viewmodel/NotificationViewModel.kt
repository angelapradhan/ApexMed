package com.example.doctors.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.doctors.model.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NotificationViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // List for UI observation
    val notificationsList = mutableStateListOf<NotificationModel>()

    init {
        fetchNotifications()
    }

    // Listen for real-time notifications
    fun fetchNotifications() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users").document(userId)
            .collection("notifications")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    notificationsList.clear()
                    val docs = snapshot.toObjects(NotificationModel::class.java)
                    notificationsList.addAll(docs)
                }
            }
    }
}