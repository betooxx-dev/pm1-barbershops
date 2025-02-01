package com.example.moviles01.view.screens.barbershop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.view.components.LoadingSpinner
import com.example.moviles01.view.screens.barbershop.components.BarbershopCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarbershopScreen(
    barbershops: List<Barbershop>,
    isLoading: Boolean,
    onAddClick: () -> Unit,
    onEditClick: (Barbershop) -> Unit,
    onDeleteClick: (String) -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Barberías") },
            actions = {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                LoadingSpinner()
            } else {
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
                            onDeleteClick = { onDeleteClick(barbershop._id) }
                        )
                    }
                }

                FloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar barbería"
                    )
                }
            }
        }
    }
}