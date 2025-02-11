package com.example.moviles01.model.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.network.ApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

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

    // BarbershopRepository.kt
    suspend fun uploadImage(imageUri: Uri, context: Context): Result<String> {
        return try {
            // Obtenemos el nombre real del archivo
            val fileName = context.contentResolver.query(imageUri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            } ?: "image.jpg"

            // Creamos un stream del archivo
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val byteArray = inputStream?.readBytes() ?: throw Exception("No se pudo leer el archivo")

            // Creamos el RequestBody
            val requestBody = byteArray.toRequestBody(
                "image/*".toMediaTypeOrNull(),
                0,
                byteArray.size
            )

            // Creamos el MultipartBody.Part
            val filePart = MultipartBody.Part.createFormData(
                "file",
                fileName,
                requestBody
            )

            val response = apiService.uploadImage(filePart)
            if (response.isSuccessful && response.body() != null) {
                // Modificar esta línea para usar el nuevo nombre de archivo
                Result.success(response.body()!!["fileName"] ?: "")
            } else {
                Result.failure(Exception("Error al subir la imagen"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}