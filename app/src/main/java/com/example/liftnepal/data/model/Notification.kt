package com.example.liftnepal.data.model

data class Notification(
    val id: String = "",
    val userId: String = "", // Target user who receives the notification
    val title: String = "",
    val message: String = "",
    val type: String = "", // verification, ride_booked, ride_cancelled
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val relatedId: String = "" // Ride ID or Verification ID
)