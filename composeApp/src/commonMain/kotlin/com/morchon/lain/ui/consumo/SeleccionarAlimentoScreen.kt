package com.morchon.lain.ui.consumo

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.model.Receta
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarAlimentoScreen(
    onNavigateBack: () -> Unit,
    viewModel: SeleccionarAlimentoViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Alimentos", "Recetas", "Planes")

    if (estado.guardadoExitoso) {
        LaunchedEffect(Unit) {
            onNavigateBack()
            viewModel.resetExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Consumo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Buscador
            OutlinedTextField(
                value = estado.query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar alimento o receta...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            // Tabs
            TabRow(selectedTabIndex = tabSeleccionada) {
                titulosTabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionada == index,
                        onClick = { tabSeleccionada = index },
                        text = { Text(titulo) }
                    )
                }
            }

            when (tabSeleccionada) {
                0, 1 -> {
                    // Listado de Alimentos o Recetas (usamos la misma lógica por polimorfismo)
                    val filtrados = if (tabSeleccionada == 0) {
                        estado.listaResultados.filter { it !is Receta }
                    } else {
                        estado.listaResultados.filterIsInstance<Receta>()
                    }

                    if (estado.cargando) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (filtrados.isEmpty() && estado.query.length > 2) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No se encontraron resultados")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filtrados) { consumible ->
                                ConsumibleItem(
                                    alimento = consumible,
                                    onClick = { viewModel.seleccionarAlimento(consumible) }
                                )
                                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
                2 -> {
                    // Listado de Planes
                    if (estado.planes.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No tienes planes creados")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(estado.planes) { plan ->
                                Card(
                                    modifier = Modifier.fillMaxWidth().clickable { viewModel.seleccionarPlan(plan) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    ListItem(
                                        headlineContent = { Text(plan.nombre, fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text("${plan.tipo} - ${plan.items.size} alimentos") },
                                        trailingContent = {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = "Ver detalles",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de configuración de alimento suelto
    estado.alimentoSeleccionado?.let { alimento ->
        DialogoConfigurarConsumo(
            alimento = alimento,
            cantidad = estado.cantidadGramos,
            momento = estado.momentoComida,
            onCantidadChange = viewModel::onCantidadChange,
            onMomentoChange = viewModel::onMomentoChange,
            onConfirmar = viewModel::confirmarConsumo,
            onDismiss = { viewModel.seleccionarAlimento(null) }
        )
    }

    // NUEVO: Diálogo de detalle de plan
    estado.planSeleccionado?.let { plan ->
        DialogoDetallePlan(
            plan = plan,
            onRegistrarItem = viewModel::registrarItemDePlan,
            onAplicarTodo = {
                viewModel.confirmarAplicarPlanCompleto()
                viewModel.seleccionarPlan(null)
            },
            onDismiss = { viewModel.seleccionarPlan(null) }
        )
    }
}

@Composable
fun DialogoDetallePlan(
    plan: Plan,
    onRegistrarItem: (ItemPlan) -> Unit,
    onAplicarTodo: () -> Unit,
    onDismiss: () -> Unit
) {
    val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val itemsAMostrar = if (plan.tipo == "SEMANAL") {
        plan.items.filter { it.indiceDia == hoy.dayOfWeek.isoDayNumber }
    } else {
        plan.items
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(plan.nombre) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                Text(
                    text = if (plan.tipo == "SEMANAL") "Sugerencias para hoy (${hoy.dayOfWeek})" else "Contenido del plan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                if (itemsAMostrar.isEmpty()) {
                    Text("No hay alimentos definidos para hoy en este plan.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(itemsAMostrar) { item ->
                            ListItem(
                                headlineContent = { Text(item.alimento.nombre, fontWeight = FontWeight.SemiBold) },
                                supportingContent = { Text("${item.cantidadGramos.toInt()}g - ${item.momentoComida.name.lowercase()}") },
                                trailingContent = {
                                    IconButton(onClick = { onRegistrarItem(item) }) {
                                        Icon(Icons.Default.Add, contentDescription = "Añadir este alimento", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onAplicarTodo, enabled = itemsAMostrar.isNotEmpty()) {
                Text("Añadir todo el día")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
fun ConsumibleItem(alimento: Alimento, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(alimento.nombre, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Text("${alimento.kcalPor100g.toInt()} kcal | P: ${alimento.proteinasPor100g.toInt()}g | HC: ${alimento.carbohidratosPor100g.toInt()}g | G: ${alimento.grasasPor100g.toInt()}g (por 100g)")
        },
        trailingContent = {
            if (alimento is Receta) {
                SuggestionChip(onClick = {}, label = { Text("Receta") })
            }
        }
    )
}

@Composable
fun DialogoConfigurarConsumo(
    alimento: Alimento,
    cantidad: String,
    momento: MomentoComida,
    onCantidadChange: (String) -> Unit,
    onMomentoChange: (MomentoComida) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Ingesta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(alimento.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                
                OutlinedTextField(
                    value = cantidad,
                    onValueChange = onCantidadChange,
                    label = { Text("Cantidad (gramos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Momento de la comida:", style = MaterialTheme.typography.labelLarge)
                FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MomentoComida.entries.forEach { m ->
                        FilterChip(
                            selected = momento == m,
                            onClick = { onMomentoChange(m) },
                            label = { Text(m.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                // Resumen de lo que se va a añadir
                val gramos = cantidad.toDoubleOrNull() ?: 0.0
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Se añadirán:", style = MaterialTheme.typography.labelSmall)
                        Text("${alimento.calcularKcal(gramos).toInt()} kcal | ${alimento.calcularProteinas(gramos).toInt()}g Prot", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirmar) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
