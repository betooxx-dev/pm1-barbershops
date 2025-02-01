package com.example.moviles01.viewmodel.barbershop

import com.example.moviles01.model.data.Barbershop

data class BarbershopState(
    val barbershops: List<Barbershop> = emptyList(),
    val selectedBarbershop: Barbershop? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleteDialogVisible: Boolean = false,
    val barbershopToDelete: String? = null
)