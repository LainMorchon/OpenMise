package com.morchon.lain.ui.planes.editar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.ui.consumo.SeleccionarAlimentoViewModel
import com.morchon.lain.ui.consumo.ConsumibleItem
import com.morchon.lain.ui.consumo.DialogoConfigurarConsumo
import com.morchon.lain.ui.consumo.CustomSnackbarHost
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPlanScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditarPlanViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val buscadorViewModel: SeleccionarAlimentoViewModel = koinViewModel()
    val buscadorEstado by buscadorViewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.consumirError()
        }
    }

    LaunchedEffect(state.mensajeExito) {
        state.mensajeExito?.let {
            snackbarHostState.showSnackbar(message = it)
            viewModel.consumirMensaje()
        }
    }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onNavigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = state.plan.nombre,
                                onValueChange = { viewModel.onNombreChanged(it) },
                                label = { Text("Nombre del Plan") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Tipo de Plan:", style = MaterialTheme.typography.labelLarge)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("DIA_UNICO", "SEMANAL").forEach { tipo ->
                                    FilterChip(
                                        selected = state.plan.tipo == tipo,
                                        onClick = { viewModel.onTipoChanged(tipo) },
                                        label = { Text(tipo.replace("_", " ")) }
                                    )
                                }
                            }

                            if (state.plan.tipo == "SEMANAL") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    (1..7).forEach { dia ->
                                        FilterChip(
                                            selected = state.indiceDiaSeleccionado == dia,
                                            onClick = { viewModel.onDiaSeleccionado(dia) },
                                            label = { Text("Día $dia") }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.plan.tipo == "SEMANAL") "Alimentos Día ${state.indiceDiaSeleccionado}" else "Alimentos en el plan",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        if (state.listaPlanesDisponibles.isNotEmpty()) {
                            TextButton(onClick = { viewModel.toggleDialogoPlantilla(true) }) {
                                Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Usar plantilla")
                            }
                        }
                    }

                    val itemsFiltrados = remember(state.plan.items, state.indiceDiaSeleccionado) {
                        state.plan.items.filter { it.indiceDia == state.indiceDiaSeleccionado }
                    }

                    if (itemsFiltrados.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No hay alimentos para este día", color = MaterialTheme.outlineVariant())
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            items(itemsFiltrados) { item ->
                                ItemPlanRow(item = item, onDelete = { viewModel.eliminarItem(item) })
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                    
                    ResumenMacrosPlan(items = itemsFiltrados)
                }
            }
        }

        CustomSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center).padding(32.dp)
        )
    }

    if (state.showDialogoSeleccionarPlantilla) {
        DialogoSeleccionarPlantilla(
            planes = state.listaPlanesDisponibles,
            onPlanSelected = { viewModel.aplicarPlantillaADia(it) },
            onDismiss = { viewModel.toggleDialogoPlantilla(false) },
            snackbarHostState = snackbarHostState
        )
    }

    if (state.showBuscadorAlimentos) {
        ModalBuscadorAlimentos(
            estado = buscadorEstado,
            onQueryChange = buscadorViewModel::onQueryChange,
            onAlimentoSelected = buscadorViewModel::seleccionarAlimento,
            onDismiss = { viewModel.toggleBuscador(false) },
            snackbarHostState = snackbarHostState
        )
    }

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
            onDismiss = { buscadorViewModel.seleccionarAlimento(null) },
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun DialogoSeleccionarPlantilla(
    planes: List<Plan>,
    onPlanSelected: (Plan) -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Copiar desde Plan") },
        text = {
            Box(contentAlignment = Alignment.Center) {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                    items(planes) { plan ->
                        ListItem(
                            headlineContent = { Text(plan.nombre) },
                            supportingContent = { Text("${plan.items.size} alimentos - ${plan.tipo}") },
                            modifier = Modifier.clickable { onPlanSelected(plan) }
                        )
                    }
                }
                CustomSnackbarHost(snackbarHostState)
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
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
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar Alimento") },
        text = {
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    OutlinedTextField(
                        value = estado.query,
                        onValueChange = onQueryChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej: Pollo, Arroz...") },
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
                CustomSnackbarHost(snackbarHostState)
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun ResumenMacrosPlan(items: List<ItemPlan>) {
    val kcal = items.sumOf { it.alimento.calcularKcal(it.cantidadGramos) }
    val prot = items.sumOf { it.alimento.calcularProteinas(it.cantidadGramos) }
    
    Surface(color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Total Selección:", fontWeight = FontWeight.Bold)
            Text("${kcal.toInt()} kcal | ${prot.toInt()}g Proteína", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MaterialTheme.outlineVariant() = colorScheme.outline.copy(alpha = 0.6f)
