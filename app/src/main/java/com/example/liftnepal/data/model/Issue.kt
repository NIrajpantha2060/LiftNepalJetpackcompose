package com.example.liftnepal.data.model



data class Issue(
    val issueId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String = "",
    val userPhone: String = "",
    val userType: String = "user",        // "user" or "rider"
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val screenshotUrl: String = "",
    val status: String = "open",          // "open" or "resolved"
    val createdAt: Long = System.currentTimeMillis()
)