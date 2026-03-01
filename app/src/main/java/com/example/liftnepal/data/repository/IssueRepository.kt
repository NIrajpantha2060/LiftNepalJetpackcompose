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

    suspend fun updateIssueStatus(issueId: String, status: String): Result<Boolean> {
        return try {
            // Fetch issue first so we can notify the right user
            val snapshot = db.child("issues").child(issueId).get().await()
            val issue = snapshot.getValue(Issue::class.java)
                ?: return Result.Error("Issue not found")

            // Update status in Firebase
            db.child("issues").child(issueId).child("status").setValue(status).await()

            // Send notification to user if issue is resolved
            if (status == "resolved" && issue.userId.isNotEmpty()) {
                notificationRepo.sendNotification(
                    Notification(
                        userId    = issue.userId,
                        title     = "Issue Resolved ✅",
                        message   = "Your issue \"${issue.title}\" has been reviewed and resolved by the admin.",
                        type      = "issue_resolved",
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