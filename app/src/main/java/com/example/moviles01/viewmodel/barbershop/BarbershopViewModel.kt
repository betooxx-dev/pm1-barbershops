package com.example.moviles01.viewmodel.barbershop

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.repository.BarbershopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BarbershopViewModel(
    private val repository: BarbershopRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BarbershopState())
    val state: StateFlow<BarbershopState> = _state.asStateFlow()

    init {
        loadBarbershops()
    }

    fun loadBarbershops() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.getBarbershops()
                result.fold(
                    onSuccess = { barbershops ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            barbershops = barbershops
                        )
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar las barberías"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun createBarbershop(barbershop: Barbershop) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.createBarbershop(barbershop)
                result.fold(
                    onSuccess = { _ ->
                        loadBarbershops()
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al crear la barbería"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun updateBarbershop(barbershop: Barbershop) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.updateBarbershop(barbershop)
                result.fold(
                    onSuccess = { _ ->
                        loadBarbershops()
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al actualizar la barbería"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun deleteBarbershop(id: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val result = repository.deleteBarbershop(id)
                result.fold(
                    onSuccess = { _ ->
                        loadBarbershops()
                    },
                    onFailure = { exception ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al eliminar la barbería"
                        )
                    }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun showDeleteConfirmation(barbershopId: String) {
        _state.value = _state.value.copy(
            isDeleteDialogVisible = true,
            barbershopToDelete = barbershopId
        )
    }

    fun hideDeleteConfirmation() {
        _state.value = _state.value.copy(
            isDeleteDialogVisible = false,
            barbershopToDelete = null
        )
    }

    suspend fun uploadImage(uri: Uri, context: Context): Result<String> {
        return try {
            repository.uploadImage(uri, context)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun selectBarbershop(barbershop: Barbershop?) {
        _state.value = _state.value.copy(selectedBarbershop = barbershop)
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}