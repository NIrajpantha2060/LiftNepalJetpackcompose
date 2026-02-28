package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.model.Verification
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

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

    // ─── Verifications Table ──────────────────────────────────────

    // verifications/{uid}  ← uid is foreign key → users/{uid}

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

    // Admin: fetch all verifications joined with user info
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

    suspend fun updateVerificationStatus(uid: String, status: String): Result<Boolean> {
        return try {
            db.child("verifications").child(uid).child("status").setValue(status).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update status")
        }
    }
}