package com.example.liftnepal.data.model

data class Vehicle(
    val vehicleNumber: String = "",
    val vehiclePhotoUrl: String = "",
    val vehicleType: String = "Two Wheeler", // e.g., "Two Wheeler", "Four Wheeler"
    val updatedAt: Long = System.currentTimeMillis()
)