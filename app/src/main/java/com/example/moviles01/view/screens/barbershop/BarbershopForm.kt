package com.example.moviles01.view.screens.barbershop

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.data.Contact
import com.example.moviles01.model.data.Location
import com.example.moviles01.model.data.WorkingDays
import com.example.moviles01.view.components.ErrorDialog
import com.example.moviles01.view.components.LoadingSpinner
import com.example.moviles01.view.screens.barbershop.components.ImagePicker
import kotlinx.coroutines.launch

@Composable
fun BarbershopForm(
    barbershop: Barbershop? = null,
    isLoading: Boolean = false,
    onSubmit: (Barbershop) -> Unit,
    onCancel: () -> Unit,
    onUploadImage: suspend (Uri) -> Result<String>
) {
    var name by remember { mutableStateOf(barbershop?.name ?: "") }
    var description by remember { mutableStateOf(barbershop?.description ?: "") }
    var city by remember { mutableStateOf(barbershop?.location?.city ?: "") }
    var street by remember { mutableStateOf(barbershop?.location?.street ?: "") }
    var phone by remember { mutableStateOf(barbershop?.contact?.phone ?: "") }
    var email by remember { mutableStateOf(barbershop?.contact?.email ?: "") }
    var days by remember { mutableStateOf(barbershop?.workingDays?.days ?: "") }
    var schedule by remember { mutableStateOf(barbershop?.workingDays?.schedule ?: "") }
    var logo by remember { mutableStateOf(barbershop?.logo ?: "") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isImageUploading by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (isLoading || isImageUploading) {
        LoadingSpinner()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = if (barbershop == null) "Nueva Barbería" else "Editar Barbería",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sección de imagen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Logo",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Preview de la imagen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                ) {
                    when {
                        // Muestra la imagen seleccionada pero aún no subida
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Vista previa",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        // Muestra la imagen existente
                        logo.isNotEmpty() -> {
                            AsyncImage(
                                model = "http://10.0.2.2:3000/barbershop/image/$logo",
                                contentDescription = "Logo actual",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }
                        // Muestra un placeholder cuando no hay imagen
                        else -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin imagen",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Botón para seleccionar imagen
                ImagePicker { uri ->
                    selectedImageUri = uri
                    scope.launch {
                        isImageUploading = true
                        onUploadImage(uri).fold(
                            onSuccess = { fileName ->
                                logo = fileName
                                selectedImageUri = null  // Limpiamos el URI temporal
                                isImageUploading = false
                            },
                            onFailure = { exception ->
                                errorMessage = exception.message ?: "Error al subir la imagen"
                                showError = true
                                isImageUploading = false
                            }
                        )
                    }
                }
            }
        }

        // Información básica
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Información básica",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        }

        // Ubicación
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Ubicación",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Contacto
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Contacto",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Horario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Horario",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Días de trabajo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Horario") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Botones de acción
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank() || city.isBlank() ||
                        street.isBlank() || phone.isBlank() || email.isBlank() ||
                        days.isBlank() || schedule.isBlank() || logo.isBlank()
                    ) {
                        errorMessage = "Por favor complete todos los campos"
                        showError = true
                        return@Button
                    }

                    val newBarbershop = Barbershop(
                        _id = barbershop?._id ?: "",
                        name = name,
                        description = description,
                        location = Location(city, street),
                        services = barbershop?.services ?: emptyList(),
                        workingDays = WorkingDays(days, schedule),
                        contact = Contact(phone, email),
                        logo = logo,
                        photos = barbershop?.photos ?: emptyList(),
                        owner = barbershop?.owner ?: "",
                        appointments = barbershop?.appointments ?: emptyList(),
                        reviews = barbershop?.reviews ?: emptyList(),
                        payments = barbershop?.payments ?: emptyList()
                    )
                    onSubmit(newBarbershop)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (barbershop == null) "Crear" else "Actualizar")
            }
        }
    }

    if (showError) {
        ErrorDialog(
            errorMessage = errorMessage,
            onDismiss = { showError = false }
        )
    }
}