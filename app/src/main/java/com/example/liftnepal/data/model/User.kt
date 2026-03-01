package com.example.liftnepal.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val profilePhotoUrl: String = "",
    val vehicle: Vehicle? = null,       // ← Saved vehicle info
    val strikeCount: Int = 0            // ← Strike count added by admin
)