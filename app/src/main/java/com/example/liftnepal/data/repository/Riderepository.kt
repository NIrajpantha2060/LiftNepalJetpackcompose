package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.utils.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RideRepository {

    private val db = FirebaseDatabase.getInstance().reference
    private val notificationRepo = NotificationRepository()

    suspend fun addRide(ride: Ride): Result<Boolean> {
        return try {
            val rideId = db.child("rides").push().key ?: return Result.Error("Failed to generate ride ID")
            val rideWithId = ride.copy(rideId = rideId)
            db.child("rides").child(rideId).setValue(rideWithId).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add ride")
        }
    }

    suspend fun getAllActiveRides(): Result<List<Ride>> {
        return try {
            val snapshot = db.child("rides")
                .orderByChild("status")
                .equalTo("active")
                .get()
                .await()

            val rides = snapshot.children.mapNotNull { it.getValue(Ride::class.java) }
            Result.Success(rides.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch rides")
        }
    }

    // ✅ NEW: Fetches ALL rides for admin regardless of status
    suspend fun getAllRides(): Result<List<Ride>> {
        return try {
            val snapshot = db.child("rides").get().await()
            val rides = snapshot.children.mapNotNull { it.getValue(Ride::class.java) }
            Result.Success(rides.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch all rides")
        }
    }

    suspend fun getRidesByRider(riderId: String): Result<List<Ride>> {
        return try {
            val snapshot = db.child("rides")
                .orderByChild("riderId")
                .equalTo(riderId)
                .get()
                .await()

            val rides = snapshot.children.mapNotNull { it.getValue(Ride::class.java) }
            Result.Success(rides.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch rider's rides")
        }
    }

    suspend fun getRidesByPassenger(userId: String): Result<List<Ride>> {
        return try {
            val snapshot = db.child("rides")
                .orderByChild("bookedBy")
                .equalTo(userId)
                .get()
                .await()

            val rides = snapshot.children.mapNotNull { it.getValue(Ride::class.java) }
            Result.Success(rides.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch bookings")
        }
    }

    suspend fun updateRideStatus(rideId: String, status: String, cancelledBy: String = ""): Result<Boolean> {
        return try {
            val snapshot = db.child("rides").child(rideId).get().await()
            val ride = snapshot.getValue(Ride::class.java) ?: return Result.Error("Ride not found")

            val updates = mutableMapOf<String, Any>("status" to status)
            if (cancelledBy.isNotEmpty()) updates["cancelledBy"] = cancelledBy
            db.child("rides").child(rideId).updateChildren(updates).await()

            // Notify if cancelled
            if (status == "cancelled") {
                if (cancelledBy == "rider" && ride.bookedBy.isNotEmpty()) {
                    notificationRepo.sendNotification(Notification(
                        userId = ride.bookedBy,
                        title = "Ride Cancelled",
                        message = "Your ride from ${ride.startLocation} has been cancelled by the rider.",
                        type = "ride_cancelled",
                        relatedId = rideId
                    ))
                } else if (cancelledBy == "passenger") {
                    notificationRepo.sendNotification(Notification(
                        userId = ride.riderId,
                        title = "Booking Cancelled",
                        message = "Your ride to ${ride.destination} was cancelled by ${ride.passengerName}.",
                        type = "ride_cancelled",
                        relatedId = rideId
                    ))
                }
            }

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update ride status")
        }
    }

    suspend fun bookRide(
        rideId: String, 
        userId: String, 
        userName: String, 
        userPhone: String, 
        userPhotoUrl: String
    ): Result<Boolean> {
        return try {
            val snapshot = db.child("rides").child(rideId).get().await()
            val ride = snapshot.getValue(Ride::class.java) ?: return Result.Error("Ride not found")

            val updates = mapOf(
                "bookedBy" to userId,
                "passengerName" to userName,
                "passengerPhone" to userPhone,
                "passengerPhotoUrl" to userPhotoUrl,
                "status" to "booked"
            )
            db.child("rides").child(rideId).updateChildren(updates).await()

            // Send notification to rider
            notificationRepo.sendNotification(Notification(
                userId = ride.riderId,
                title = "Ride Booked!",
                message = "Your ride to ${ride.destination} has been booked by $userName.",
                type = "ride_booked",
                relatedId = rideId
            ))

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to book ride")
        }
    }

    suspend fun cancelBooking(rideId: String, status: String = "active", cancelledBy: String = ""): Result<Boolean> {
        return try {
            val snapshot = db.child("rides").child(rideId).get().await()
            val ride = snapshot.getValue(Ride::class.java) ?: return Result.Error("Ride not found")

            val updates = mutableMapOf<String, Any?>(
                "status" to status
            )
            if (status == "active") {
                updates["bookedBy"] = ""
                updates["passengerName"] = ""
                updates["passengerPhone"] = ""
                updates["passengerPhotoUrl"] = ""
            }
            if (cancelledBy.isNotEmpty()) updates["cancelledBy"] = cancelledBy
            
            db.child("rides").child(rideId).updateChildren(updates).await()

            // Send notification to rider if passenger cancels a booking
            if (cancelledBy == "passenger") {
                notificationRepo.sendNotification(Notification(
                    userId = ride.riderId,
                    title = "Ride Cancelled",
                    message = "Your booking for ride to ${ride.destination} was cancelled by ${ride.passengerName}.",
                    type = "ride_cancelled",
                    relatedId = rideId
                ))
            }

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to cancel booking")
        }
    }

    suspend fun deleteRide(rideId: String): Result<Boolean> {
        return try {
            db.child("rides").child(rideId).removeValue().await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete ride")
        }
    }
}