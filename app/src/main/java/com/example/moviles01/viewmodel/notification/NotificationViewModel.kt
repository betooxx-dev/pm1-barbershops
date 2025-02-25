package com.example.moviles01.viewmodel.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.moviles01.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _notificationState = MutableStateFlow(NotificationState())
    val notificationState: StateFlow<NotificationState> = _notificationState.asStateFlow()

    init {
        refreshFcmToken()
    }

    fun refreshFcmToken() {
        viewModelScope.launch {
            authRepository.updateFcmToken()
        }
    }

    fun clearNotifications() {
        _notificationState.value = _notificationState.value.copy(
            unreadCount = 0
        )
    }

    data class NotificationState(
        val unreadCount: Int = 0,
        val lastNotification: String? = null
    )
}

class NotificationViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}