package com.example.moviles01.viewmodel.barbershop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moviles01.model.repository.BarbershopRepository

class BarbershopViewModelFactory(
    private val repository: BarbershopRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BarbershopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BarbershopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}