package com.morchon.lain.ui.diario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.RegistroDiario
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiarioScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiarioViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Consumo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Selector de Fecha
            SelectorFecha(
                fecha = estado.fechaSeleccionada.toString(),
                onAnterior = { viewModel.alCambiarFecha(-1) },
                onSiguiente = { viewModel.alCambiarFecha(1) }
            )

            // Resumen de Macros
            ResumenMacros(
                kcal = estado.totalKcal,
                proteinas = estado.totalProteinas,
                carbohidratos = estado.totalCarbohidratos,
                grasas = estado.totalGrasas
            )

            if (estado.registros.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay registros para este día", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val agrupados = estado.registros.groupBy { it.momentoComida }
                    
                    MomentoComida.entries.forEach { momento ->
                        val registrosMomento = agrupados[momento] ?: emptyList()
                        if (registrosMomento.isNotEmpty()) {
                            item {
                                Text(
                                    text = momento.name.replace("_", " "),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(registrosMomento) { registro ->
                                ItemRegistro(
                                    registro = registro,
                                    onEliminar = { viewModel.eliminarRegistro(registro.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SelectorFecha(
    fecha: String,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onAnterior) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Día anterior")
        }
        Text(
            text = fecha,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onSiguiente) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Día siguiente")
        }
    }
}

@Composable
fun ItemRegistro(
    registro: RegistroDiario,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = registro.nombreAlimento,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${registro.cantidadGramos}g - ${registro.kcalHistoricas.toInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "P: ${registro.proteinasHistoricas}g | H: ${registro.carbohidratosHistoricos}g | G: ${registro.grasasHistoricas}g",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ResumenMacros(
    kcal: Double,
    proteinas: Double,
    carbohidratos: Double,
    grasas: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${kcal.toInt()}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "kcal totales",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MacroMiniDato(label = "P", valor = proteinas)
                MacroMiniDato(label = "H", valor = carbohidratos)
                MacroMiniDato(label = "G", valor = grasas)
            }
        }
    }
}

@Composable
fun MacroMiniDato(label: String, valor: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "${valor.toInt()}g",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}
