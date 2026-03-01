package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Vehicle
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    private val notificationRepo = NotificationRepository()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // ─── Auth ────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user!!)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(
        username: String,
        email: String,
        password: String,
        phoneNumber: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            val userModel = User(
                uid = user.uid,
                email = email,
                displayName = username,
                phoneNumber = phoneNumber
            )
            db.child("users").child(user.uid).setValue(userModel).await()
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Signup failed")
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun sendPasswordReset(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send reset email")
        }
    }

    // ─── Users Table ─────────────────────────────────────────────

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val snapshot = db.child("users").get().await()
            val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
            Result.Success(users)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch users")
        }
    }

    suspend fun deleteUser(userId: String): Result<Boolean> {
        return try {
            db.child("users").child(userId).removeValue().await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to delete user")
        }
    }

    suspend fun getCurrentUserData(uid: String): Result<User> {
        return try {
            val snapshot = db.child("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null) Result.Success(user)
            else Result.Error("User not found")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch user")
        }
    }

    suspend fun updateProfilePhoto(uid: String, photoUrl: String): Result<Boolean> {
        return try {
            db.child("users").child(uid).child("profilePhotoUrl").setValue(photoUrl).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update profile photo")
        }
    }

    suspend fun updateVehicleInfo(uid: String, vehicle: Vehicle): Result<Boolean> {
        return try {
            db.child("users").child(uid).child("vehicle").setValue(vehicle).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update vehicle info")
        }
    }

    // ─── Strike System ────────────────────────────────────────────

    // ✅ NEW: Add a strike to user. Also cancels verification if strikeCount reaches 3.
    suspend fun addStrike(uid: String): Result<Int> {
        return try {
            val snapshot = db.child("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java) ?: return Result.Error("User not found")
            val newStrikeCount = user.strikeCount + 1

            db.child("users").child(uid).child("strikeCount").setValue(newStrikeCount).await()

            // Notify user about strike
            notificationRepo.sendNotification(Notification(
                userId = uid,
                title = "⚠️ Strike Warning",
                message = "You have received a strike from the admin. Total strikes: $newStrikeCount. Please follow community guidelines.",
                type = "strike"
            ))

            // If 3 or more strikes, auto-cancel verification
            if (newStrikeCount >= 3) {
                val verSnapshot = db.child("verifications").child(uid).get().await()
                if (verSnapshot.exists()) {
                    db.child("verifications").child(uid).child("status").setValue("rejected").await()
                    db.child("verifications").child(uid).child("remarks").setValue("Verification cancelled due to 3 or more strikes.").await()
                    notificationRepo.sendNotification(Notification(
                        userId = uid,
                        title = "Verification Cancelled",
                        message = "Your verification has been cancelled automatically due to receiving 3 or more strikes.",
                        type = "verification"
                    ))
                }
            }

            Result.Success(newStrikeCount)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to add strike")
        }
    }

    // ✅ NEW: Remove a strike from user (undo)
    suspend fun removeStrike(uid: String): Result<Int> {
        return try {
            val snapshot = db.child("users").child(uid).get().await()
            val user = snapshot.getValue(User::class.java) ?: return Result.Error("User not found")
            val newStrikeCount = maxOf(0, user.strikeCount - 1)

            db.child("users").child(uid).child("strikeCount").setValue(newStrikeCount).await()

            notificationRepo.sendNotification(Notification(
                userId = uid,
                title = "Strike Removed",
                message = "A strike has been removed from your account. Total strikes: $newStrikeCount.",
                type = "strike"
            ))

            Result.Success(newStrikeCount)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to remove strike")
        }
    }

    // ✅ NEW: Manually cancel a user's verification
    suspend fun cancelVerification(uid: String, reason: String): Result<Boolean> {
        return try {
            val verSnapshot = db.child("verifications").child(uid).get().await()
            if (!verSnapshot.exists()) return Result.Error("No verification found for this user")

            val updates = mapOf(
                "status" to "rejected",
                "remarks" to reason
            )
            db.child("verifications").child(uid).updateChildren(updates).await()

            notificationRepo.sendNotification(Notification(
                userId = uid,
                title = "Verification Cancelled",
                message = "Your verification has been cancelled by the admin. Reason: $reason",
                type = "verification"
            ))

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to cancel verification")
        }
    }

    // ─── Verifications Table ──────────────────────────────────────

    suspend fun submitVerification(
        uid: String,
        licenseNumber: String,
        licenseExpiryDate: String,
        licensePhotoUrl: String
    ): Result<Boolean> {
        return try {
            val verification = Verification(
                uid = uid,
                licenseNumber = licenseNumber,
                licenseExpiryDate = licenseExpiryDate,
                licensePhotoUrl = licensePhotoUrl,
                status = "pending",
                submittedAt = System.currentTimeMillis()
            )
            db.child("verifications").child(uid).setValue(verification).await()

            notificationRepo.sendNotification(Notification(
                userId = uid,
                title = "Verification Submitted",
                message = "Your documents have been submitted and are under review by the admin.",
                type = "verification"
            ))

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to submit verification")
        }
    }

    suspend fun getVerification(uid: String): Result<Verification?> {
        return try {
            val snapshot = db.child("verifications").child(uid).get().await()
            Result.Success(snapshot.getValue(Verification::class.java))
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch verification")
        }
    }

    suspend fun getAllVerifications(): Result<List<Pair<User, Verification>>> {
        return try {
            val verSnapshot = db.child("verifications").get().await()
            val result = mutableListOf<Pair<User, Verification>>()
            for (child in verSnapshot.children) {
                val verification = child.getValue(Verification::class.java) ?: continue
                val userSnapshot = db.child("users").child(verification.uid).get().await()
                val user = userSnapshot.getValue(User::class.java) ?: continue
                result.add(Pair(user, verification))
            }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch verifications")
        }
    }

    suspend fun updateVerificationStatus(uid: String, status: String, remarks: String = ""): Result<Boolean> {
        return try {
            val updates = mutableMapOf<String, Any>("status" to status)
            if (remarks.isNotEmpty()) updates["remarks"] = remarks
            db.child("verifications").child(uid).updateChildren(updates).await()

            val title = if (status == "approved") "Verification Approved!" else "Verification Rejected"
            val message = if (status == "approved") "Congratulations! You are now a verified rider."
            else "Sorry, your verification was rejected. Remarks: $remarks"

            notificationRepo.sendNotification(Notification(
                userId = uid,
                title = title,
                message = message,
                type = "verification"
            ))

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update status")
        }
    }
}