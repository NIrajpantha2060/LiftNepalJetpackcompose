package com.example.liftnepal.data.model

data class Ride(
    val rideId: String = "",
    val riderId: String = "",
    val riderName: String = "",
    val riderPhone: String = "",
    val riderPhotoUrl: String = "",

    val vehicleNumber: String = "",
    val vehiclePhotoUrl: String = "",

    val startLocation: String = "",
    val destination: String = "",
    val pickupLocation: String = "",
    val rideTime: String = "",              // ← NEW: e.g. "08:00 PM"

    val remarks: String = "",
    val cost: String = "",

    val status: String = "active",
    val createdAt: Long = System.currentTimeMillis(),

    val availableSeats: Int = 1,
    val bookedBy: String = ""
)