package com.example.moviles01.view.screens.barbershop.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.moviles01.model.data.Barbershop

@Composable
fun BarbershopCard(
    barbershop: Barbershop,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = barbershop.logo,
                contentDescription = "Logo de ${barbershop.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = barbershop.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = barbershop.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onEditClick) {
                    Text("Editar")
                }
                TextButton(onClick = onDeleteClick) {
                    Text("Eliminar")
                }
            }
        }
    }
}