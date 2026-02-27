package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.User
import com.example.liftnepal.data.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(result.user!!)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signup(username: String, email: String, password: String, phoneNumber: String): Result<FirebaseUser> {
        return try {
            // 1. Check if phone number already exists in Realtime Database
            val phoneCheck = database.child("users")
                .orderByChild("phoneNumber")
                .equalTo(phoneNumber)
                .get()
                .await()

            if (phoneCheck.exists()) {
                return Result.Error("This phone number is already registered.")
            }

            // 2. Create user in Firebase Auth (Firebase handles duplicate emails automatically)
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // 3. Update profile with username
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
            user.updateProfile(profileUpdates).await()

            // 4. Save user data to Realtime Database
            val userData = User(
                uid = user.uid,
                email = email,
                displayName = username,
                phoneNumber = phoneNumber
            )

            database.child("users").child(user.uid).setValue(userData).await()

            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Signup failed")
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Boolean> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send reset email")
        }
    }

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun logout() = auth.signOut()
}
