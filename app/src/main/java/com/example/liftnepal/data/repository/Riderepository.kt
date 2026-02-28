package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Ride
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RideRepository {

    private val db = FirebaseDatabase.getInstance().reference

    /**
     * Add a new ride to Firebase
     * Path: rides/{rideId}
     */
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

    /**
     * Get all active rides (status = "active")
     * Returns list of rides for users to see
     */
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

    /**
     * Get rides created by a specific rider
     * For rider's history section
     */
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

    /**
     * Get a single ride by ID
     * For viewing ride details
     */
    suspend fun getRideById(rideId: String): Result<Ride?> {
        return try {
            val snapshot = db.child("rides").child(rideId).get().await()
            Result.Success(snapshot.getValue(Ride::class.java))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch ride")
        }
    }

    /**
     * Update ride status (active -> completed/cancelled)
     */
    suspend fun updateRideStatus(rideId: String, status: String): Result<Boolean> {
        return try {
            db.child("rides").child(rideId).child("status").setValue(status).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update ride status")
        }
    }

    /**
     * Delete a ride (for admin or rider)
     */
    suspend fun deleteRide(rideId: String): Result<Boolean> {
        return try {
            db.child("rides").child(rideId).removeValue().await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete ride")
        }
    }

    /**
     * Book a ride (update bookedBy field)
     * For future booking feature
     */
    suspend fun bookRide(rideId: String, userId: String): Result<Boolean> {
        return try {
            db.child("rides").child(rideId).child("bookedBy").setValue(userId).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to book ride")
        }
    }
}