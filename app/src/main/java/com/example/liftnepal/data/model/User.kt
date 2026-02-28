//
//package com.example.liftnepal.data.model
//
//data class User(
//    val uid: String = "",
//    val email: String = "",
//    val displayName: String = "",
//    val phoneNumber: String = "",
//    val createdAt: Long = System.currentTimeMillis()
//)

package com.example.liftnepal.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val phoneNumber: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val profilePhotoUrl: String = ""   // ← Cloudinary URL for profile picture
)