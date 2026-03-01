package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.Notification
import com.example.liftnepal.data.repository.NotificationRepository
import com.example.liftnepal.data.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val repository = NotificationRepository()

    private val _notifications = MutableStateFlow<Result<List<Notification>>?>(null)
    val notifications: StateFlow<Result<List<Notification>>?> = _notifications

    fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            _notifications.value = Result.Loading
            _notifications.value = repository.getNotifications(userId)
        }
    }

    fun sendNotification(notification: Notification) {
        viewModelScope.launch {
            repository.sendNotification(notification)
        }
    }

    fun markAsRead(userId: String, notificationId: String) {
        viewModelScope.launch {
            // ✅ Optimistic update — flip isRead locally immediately, no re-fetch
            val current = (_notifications.value as? Result.Success)?.data ?: emptyList()
            val updated = current.map {
                if (it.id == notificationId) it.copy(isRead = true) else it
            }
            _notifications.value = Result.Success(updated)

            // Write to Firebase in background — no fetchNotifications after
            repository.markAsRead(userId, notificationId)
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            // ✅ Optimistic update — mark all read locally immediately, no re-fetch
            val current = (_notifications.value as? Result.Success)?.data ?: emptyList()
            val updated = current.map { it.copy(isRead = true) }
            _notifications.value = Result.Success(updated)

            // Write to Firebase in background — no fetchNotifications after
            repository.markAllAsRead(userId)
        }
    }
}