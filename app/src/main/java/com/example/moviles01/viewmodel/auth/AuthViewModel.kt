package com.example.moviles01.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles01.model.data.UserData
import com.example.moviles01.model.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val userData: UserData? = null
)

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            try {
                val result = authRepository.login(email, password)
                result.fold(
                    onSuccess = { response ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            userData = response.data.user,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(isLoading = true, error = null)

            try {
                val result = authRepository.register(firstName, lastName, email, password)
                result.fold(
                    onSuccess = { response ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            userData = response.data.user,
                            error = null
                        )
                    },
                    onFailure = { exception ->
                        _authState.value = _authState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _authState.value = AuthState()
        }
    }

    fun setError(s: String) {

    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}