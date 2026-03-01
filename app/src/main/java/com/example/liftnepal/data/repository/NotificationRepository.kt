package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.utils.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class NotificationRepository {
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun sendNotification(notification: Notification): Result<Boolean> {
        return try {
            val id = db.child("notifications").child(notification.userId).push().key
                ?: return Result.Error("Failed to generate ID")
            val finalNotif = notification.copy(id = id)
            db.child("notifications").child(notification.userId).child(id).setValue(finalNotif).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send notification")
        }
    }

    suspend fun getNotifications(userId: String): Result<List<Notification>> {
        return try {
            val snapshot = db.child("notifications").child(userId).get().await()
            val notifications = snapshot.children.mapNotNull { it.getValue(Notification::class.java) }
            Result.Success(notifications.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch notifications")
        }
    }

    suspend fun markAsRead(userId: String, notificationId: String): Result<Boolean> {
        return try {
            db.child("notifications").child(userId).child(notificationId)
                .child("isRead").setValue(true).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark as read")
        }
    }

    suspend fun markAllAsRead(userId: String): Result<Boolean> {
        return try {
            val snapshot = db.child("notifications").child(userId).get().await()
            val updates = mutableMapOf<String, Any>()
            snapshot.children.forEach { child ->
                val notifId = child.key ?: return@forEach
                updates["$notifId/isRead"] = true
            }
            if (updates.isNotEmpty()) {
                db.child("notifications").child(userId).updateChildren(updates).await()
            }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to mark all as read")
        }
    }
}