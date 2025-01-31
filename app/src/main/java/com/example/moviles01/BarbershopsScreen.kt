package com.example.moviles01

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BarbershopsScreen(
    barbershops: List<Barbershop>,
    onAddClick: () -> Unit,
    onEditClick: (Barbershop) -> Unit,
    onDeleteClick: (String) -> Unit,
    onLogoutClick: () -> Unit // Nuevo parámetro
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onAddClick,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Agregar Barbería")
            }

            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(barbershops) { barbershop ->
                BarbershopCard(
                    barbershop = barbershop,
                    onEditClick = { onEditClick(barbershop) },
                    onDeleteClick = { onDeleteClick(barbershop.id) }
                )
            }
        }
    }
}

@Composable
fun BarbershopCard(
    barbershop: Barbershop,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = barbershop.logo,
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Text(
                text = barbershop.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = barbershop.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}