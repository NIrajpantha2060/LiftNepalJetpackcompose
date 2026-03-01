package com.example.liftnepal.data.model

data class Verification(
    val uid: String = "",
    val licenseNumber: String = "",
    val licenseExpiryDate: String = "",
    val licensePhotoUrl: String = "",
    val status: String = "pending",     // "pending" | "approved" | "rejected"
    val remarks: String = "",           // Admin feedback for rejection
    val submittedAt: Long = System.currentTimeMillis()
)