package com.example.liftnepal.data.repository

import com.example.liftnepal.data.model.Issue
import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.utils.Result
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class IssueRepository {

    private val db = FirebaseDatabase.getInstance().reference
    private val notificationRepo = NotificationRepository()

    suspend fun submitIssue(issue: Issue): Result<Boolean> {
        return try {
            val issueId = db.child("issues").push().key
                ?: return Result.Error("Failed to generate issue ID")
            val issueWithId = issue.copy(issueId = issueId)
            db.child("issues").child(issueId).setValue(issueWithId).await()
            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to submit issue")
        }
    }

    suspend fun getAllIssues(): Result<List<Issue>> {
        return try {
            val snapshot = db.child("issues").get().await()
            val issues = snapshot.children.mapNotNull { it.getValue(Issue::class.java) }
            Result.Success(issues.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch issues")
        }
    }

    suspend fun getIssuesByUser(userId: String): Result<List<Issue>> {
        return try {
            val snapshot = db.child("issues")
                .orderByChild("userId")
                .equalTo(userId)
                .get().await()
            val issues = snapshot.children.mapNotNull { it.getValue(Issue::class.java) }
            Result.Success(issues.sortedByDescending { it.createdAt })
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to fetch user issues")
        }
    }

    suspend fun updateIssueStatus(issueId: String, status: String, adminRemarks: String = ""): Result<Boolean> {
        return try {
            // Fetch issue first so we know who to notify
            val snapshot = db.child("issues").child(issueId).get().await()
            val issue = snapshot.getValue(Issue::class.java)
                ?: return Result.Error("Issue not found")

            // Update status + remarks together in Firebase
            val updates = mutableMapOf<String, Any>("status" to status)
            if (adminRemarks.isNotBlank()) updates["adminRemarks"] = adminRemarks
            db.child("issues").child(issueId).updateChildren(updates).await()

            // Send notification to user/rider when resolved
            if (status == "resolved" && issue.userId.isNotEmpty()) {
                val notificationType = if (issue.userType == "rider") {
                    "rider_issue_resolved"
                } else {
                    "issue_resolved"
                }

                // Include remarks in message if admin wrote one
                val notifMessage = if (adminRemarks.isNotBlank()) {
                    "Your issue \"${issue.title}\" has been resolved. Admin remarks: $adminRemarks"
                } else {
                    "Your issue \"${issue.title}\" has been reviewed and resolved by the admin."
                }

                notificationRepo.sendNotification(
                    Notification(
                        userId    = issue.userId,
                        title     = "Issue Resolved ✅",
                        message   = notifMessage,
                        type      = notificationType,
                        relatedId = issueId
                    )
                )
            }

            Result.Success(true)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to update issue status")
        }
    }
}