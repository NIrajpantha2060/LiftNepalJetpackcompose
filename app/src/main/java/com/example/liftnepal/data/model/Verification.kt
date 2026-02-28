package com.example.liftnepal.data.model



data class Verification(
    val uid: String = "",               // foreign key → users/{uid}
    val licenseNumber: String = "",
    val licenseExpiryDate: String = "",
    val licensePhotoUrl: String = "",
    val status: String = "pending",     // "pending" | "approved" | "rejected"
    val submittedAt: Long = System.currentTimeMillis()
)