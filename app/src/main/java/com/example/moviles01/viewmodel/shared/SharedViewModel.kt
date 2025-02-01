package com.example.moviles01.viewmodel.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles01.model.repository.SharedPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val prefsManager: SharedPreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(SharedState())
    val state: StateFlow<SharedState> = _state.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        val token = prefsManager.getToken()
        if (!token.isNullOrEmpty()) {
            _state.value = _state.value.copy(
                isAuthenticated = true,
                currentScreen = Screen.BarbershopList
            )
        }
    }

    fun navigate(screen: Screen) {
        _state.value = _state.value.copy(
            previousScreen = _state.value.currentScreen,
            currentScreen = screen
        )
    }

    fun navigateBack() {
        _state.value.previousScreen?.let { previousScreen ->
            _state.value = _state.value.copy(
                currentScreen = previousScreen,
                previousScreen = null
            )
        }
    }

    fun setAuthenticated(authenticated: Boolean) {
        _state.value = _state.value.copy(
            isAuthenticated = authenticated,
            currentScreen = if (authenticated) Screen.BarbershopList else Screen.Login
        )
    }

    fun logout() {
        viewModelScope.launch {
            prefsManager.clearToken()
            _state.value = SharedState()
        }
    }

    fun handleDeepLink(link: String) {
        when {
            link.contains("/barbershop/") -> {
                val barbershopId = link.substringAfterLast("/")
                navigate(Screen.BarbershopForm)
            }
            link.contains("/register") -> navigate(Screen.Register)
            link.contains("/login") -> navigate(Screen.Login)
            else -> navigate(Screen.Login)
        }
    }

    fun canNavigateBack(): Boolean {
        return _state.value.previousScreen != null
    }

    fun clearNavigationHistory() {
        _state.value = _state.value.copy(previousScreen = null)
    }
}