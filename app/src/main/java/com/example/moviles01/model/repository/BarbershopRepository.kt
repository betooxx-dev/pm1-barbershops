package com.example.moviles01.model.repository

import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.network.ApiService

class BarbershopRepository(private val apiService: ApiService) {
    suspend fun getBarbershops(): Result<List<Barbershop>> {
        return try {
            val response = apiService.getBarbershops()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar las barberías"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBarbershop(barbershop: Barbershop): Result<Barbershop> {
        return try {
            val response = apiService.createBarbershop(barbershop)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear la barbería"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBarbershop(barbershop: Barbershop): Result<Barbershop> {
        return try {
            val response = apiService.updateBarbershop(barbershop._id, barbershop)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar la barbería"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBarbershop(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteBarbershop(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar la barbería"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}