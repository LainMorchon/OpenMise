package com.morchon.lain.ui.planes.editar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.ui.consumo.SeleccionarAlimentoViewModel
import com.morchon.lain.ui.consumo.ConsumibleItem
import com.morchon.lain.ui.consumo.DialogoConfigurarConsumo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditarPlanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Necesitamos el buscador para añadir alimentos al plan
    val buscadorViewModel: SeleccionarAlimentoViewModel = koinViewModel()
    val buscadorEstado by buscadorViewModel.estado.collectAsState()

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.plan.id == 0L) "Nuevo Plan" else "Editar Plan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.guardarPlan() }) {
                        Icon(Icons.Default.Save, contentDescription = "Guardar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.toggleBuscador(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir alimento")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Cabecera: Nombre y Tipo
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.plan.nombre,
                            onValueChange = { viewModel.onNombreChanged(it) },
                            label = { Text("Nombre del Plan") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Tipo de Plan:", style = MaterialTheme.typography.labelLarge)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("DIA_UNICO", "SEMANAL").forEach { tipo ->
                                FilterChip(
                                    selected = state.plan.tipo == tipo,
                                    onClick = { viewModel.onTipoChanged(tipo) },
                                    label = { Text(tipo.replace("_", " ")) }
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "Alimentos en el plan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                if (state.plan.items.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hay alimentos en este plan", color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        items(state.plan.items) { item ->
                            ItemPlanRow(
                                item = item,
                                onDelete = { viewModel.eliminarItem(item) }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
                
                // Resumen Macros totales del plan (opcional pero útil)
                ResumenMacrosPlan(items = state.plan.items)
            }
        }
    }

    // Modal del Buscador de Alimentos
    if (state.showBuscadorAlimentos) {
        ModalBuscadorAlimentos(
            estado = buscadorEstado,
            onQueryChange = buscadorViewModel::onQueryChange,
            onAlimentoSelected = buscadorViewModel::seleccionarAlimento,
            onDismiss = { viewModel.toggleBuscador(false) }
        )
    }

    // Diálogo de configuración del alimento seleccionado en el buscador
    buscadorEstado.alimentoSeleccionado?.let { alimento ->
        DialogoConfigurarConsumo(
            alimento = alimento,
            cantidad = buscadorEstado.cantidadGramos,
            momento = buscadorEstado.momentoComida,
            onCantidadChange = buscadorViewModel::onCantidadChange,
            onMomentoChange = buscadorViewModel::onMomentoChange,
            onConfirmar = {
                viewModel.agregarAlimento(
                    alimento = alimento,
                    cantidad = buscadorEstado.cantidadGramos.toDoubleOrNull() ?: 0.0,
                    momento = buscadorEstado.momentoComida
                )
                buscadorViewModel.seleccionarAlimento(null)
            },
            onDismiss = { buscadorViewModel.seleccionarAlimento(null) }
        )
    }
}

@Composable
fun ItemPlanRow(item: ItemPlan, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(item.alimento.nombre, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Text("${item.cantidadGramos.toInt()}g - ${item.momentoComida.name.lowercase()} - ${item.alimento.calcularKcal(item.cantidadGramos).toInt()} kcal")
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun ModalBuscadorAlimentos(
    estado: com.morchon.lain.ui.consumo.SeleccionarAlimentoState,
    onQueryChange: (String) -> Unit,
    onAlimentoSelected: (com.morchon.lain.domain.model.Alimento) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar Alimento para el Plan") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                OutlinedTextField(
                    value = estado.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar...") },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (estado.cargando) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(estado.listaResultados) { alimento ->
                            ConsumibleItem(alimento = alimento, onClick = { onAlimentoSelected(alimento) })
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
fun ResumenMacrosPlan(items: List<ItemPlan>) {
    val kcal = items.sumOf { it.alimento.calcularKcal(it.cantidadGramos) }
    val prot = items.sumOf { it.alimento.calcularProteinas(it.cantidadGramos) }
    
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Plan:", fontWeight = FontWeight.Bold)
            Text("${kcal.toInt()} kcal | ${prot.toInt()}g Proteína", fontWeight = FontWeight.Bold)
        }
    }
}
