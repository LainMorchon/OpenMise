package com.morchon.lain.ui.planes.listado

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Plan
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoPlanesScreen(
    viewModel: ListadoPlanesViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCrear: () -> Unit,
    onNavigateToEditar: (Long) -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Planes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCrear) {
                Icon(Icons.Default.Add, contentDescription = "Crear Plan")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (estado.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (estado.planes.isEmpty()) {
                Text(
                    text = "No tienes planes creados todavía",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(estado.planes) { plan ->
                        TarjetaPlan(
                            plan = plan,
                            onAplicar = { viewModel.aplicarPlan(plan) },
                            onEditar = { onNavigateToEditar(plan.id) },
                            onEliminar = { viewModel.alSolicitarEliminarPlan(plan) }
                        )
                    }
                }
            }

            if (estado.mostrarDialogoEliminar) {
                AlertDialog(
                    onDismissRequest = { viewModel.cancelarEliminacion() },
                    title = { Text("Eliminar Plan") },
                    text = { Text("¿Estás seguro de que quieres eliminar el plan '${estado.planAEliminar?.nombre}'?") },
                    confirmButton = {
                        TextButton(onClick = { viewModel.confirmarEliminacion() }) {
                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.cancelarEliminacion() }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun TarjetaPlan(
    plan: Plan,
    onAplicar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditar() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plan.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tipo: ${plan.tipo}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Text(
                    text = "${plan.items.size} alimentos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row {
                IconButton(onClick = onAplicar) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Aplicar Plan",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onEliminar) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar Plan",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
