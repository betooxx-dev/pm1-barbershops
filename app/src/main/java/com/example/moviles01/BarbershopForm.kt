package com.example.moviles01

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BarbershopForm(
    barbershop: Barbershop? = null,
    onSubmit: (Barbershop) -> Unit,
    onCancel: () -> Unit
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

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Ubicación
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ubicación", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Calle") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                Text("Contacto", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
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
                Text("Horario", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(
                    value = days,
                    onValueChange = { days = it },
                    label = { Text("Días de trabajo") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("Horario") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        OutlinedTextField(
            value = logo,
            onValueChange = { logo = it },
            label = { Text("URL del Logo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val newBarbershop = Barbershop(
                        id = barbershop?.id ?: "",
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
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text(if (barbershop == null) "Crear" else "Actualizar")
            }
        }
    }
}