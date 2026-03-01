package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.utils.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RideRepository {

    private val db = FirebaseDatabase.getInstance().reference

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
            Result.Success(rides.filter { it.status == "booked" }.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch bookings")
        }
    }

    suspend fun getRideById(rideId: String): Result<Ride?> {
        return try {
            val snapshot = db.child("rides").child(rideId).get().await()
            Result.Success(snapshot.getValue(Ride::class.java))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch ride")
        }
    }

    suspend fun updateRideStatus(rideId: String, status: String): Result<Boolean> {
        return try {
            db.child("rides").child(rideId).child("status").setValue(status).await()
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
            val updates = mapOf(
                "bookedBy" to userId,
                "passengerName" to userName,
                "passengerPhone" to userPhone,
                "passengerPhotoUrl" to userPhotoUrl,
                "status" to "booked"
            )
            db.child("rides").child(rideId).updateChildren(updates).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to book ride")
        }
    }

    suspend fun cancelBooking(rideId: String): Result<Boolean> {
        return try {
            val updates = mapOf<String, Any?>(
                "bookedBy" to "",
                "passengerName" to "",
                "passengerPhone" to "",
                "passengerPhotoUrl" to "",
                "status" to "active"
            )
            db.child("rides").child(rideId).updateChildren(updates).await()
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