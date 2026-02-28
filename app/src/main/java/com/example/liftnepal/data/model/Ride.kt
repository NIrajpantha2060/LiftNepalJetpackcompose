package com.example.liftnepal.data.model

data class Ride(
    val rideId: String = "",                    // Unique ride ID
    val riderId: String = "",                   // Foreign key to User (rider who created this ride)
    val riderName: String = "",                 // Denormalized for quick display
    val riderPhone: String = "",                // Denormalized for contact
    val riderPhotoUrl: String = "",             // Denormalized rider profile photo

    // Vehicle details
    val vehicleNumber: String = "",             // e.g. "BA 1 PA 1234"
    val vehiclePhotoUrl: String = "",           // Cloudinary URL of vehicle photo

    // Route details
    val startLocation: String = "",             // Starting point
    val destination: String = "",               // End point

    // Additional info
    val remarks: String = "",                   // Description, amenities, etc.
    val cost: String = "",                      // Ride cost

    // Status
    val status: String = "active",              // active, completed, cancelled
    val createdAt: Long = System.currentTimeMillis(),

    // Booking info (for future use)
    val availableSeats: Int = 1,                // Available seats
    val bookedBy: String = ""                   // UID of user who booked (empty if not booked)
)