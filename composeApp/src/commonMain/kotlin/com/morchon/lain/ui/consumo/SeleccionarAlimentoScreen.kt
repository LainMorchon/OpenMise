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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.morchon.lain.ui.core.components.OpenMiseFilterChip
import com.morchon.lain.ui.core.components.OpenMiseTextField
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.model.Receta
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toLocalDateTime
import com.morchon.lain.ui.theme.MiseOrange
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionarAlimentoScreen(
    onNavigateBack: () -> Unit,
    viewModel: SeleccionarAlimentoViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var tabSeleccionada by remember { mutableStateOf(0) }
    val titulosTabs = listOf("Alimentos", "Recetas", "Planes")

    LaunchedEffect(estado.mensajeInformativo) {
        estado.mensajeInformativo?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.consumirMensaje()
        }
    }

    if (estado.guardadoExitoso) {
        LaunchedEffect(Unit) {
            onNavigateBack()
            viewModel.resetExito()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Añadir Consumo") },
                    navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_left_square),
                            contentDescription = "Atrás",
                            modifier = Modifier.size(30.dp),
                            tint = MiseOrange
                        )
                    }
                }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Buscador
                OpenMiseTextField(
                    value = estado.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    label = "Buscar",
                    placeholder = "Buscar alimento o receta...",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_browser),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                    }
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

                if (tabSeleccionada == 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OpenMiseFilterChip(
                            selected = estado.filtroAlimento == "all",
                            onClick = { viewModel.onFiltroChange("all") },
                            label = "Todos"
                        )
                        OpenMiseFilterChip(
                            selected = estado.filtroAlimento == "generic",
                            onClick = { viewModel.onFiltroChange("generic") },
                            label = "Crudo"
                        )
                        OpenMiseFilterChip(
                            selected = estado.filtroAlimento == "brand",
                            onClick = { viewModel.onFiltroChange("brand") },
                            label = "Marcas"
                        )
                    }
                }

                when (tabSeleccionada) {
                    0, 1 -> {
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
                                                    painter = painterResource(Res.drawable.ic_browser),
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

        // Host de notificaciones principal (para la pantalla de fondo)
        CustomSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.Center).padding(32.dp)
        )
    }

    // Diálogos con SnackbarHost interno para visibilidad total
    estado.alimentoSeleccionado?.let { alimento ->
        DialogoConfigurarConsumo(
            alimento = alimento,
            cantidad = estado.cantidadGramos,
            momento = estado.momentoComida,
            onCantidadChange = viewModel::onCantidadChange,
            onMomentoChange = viewModel::onMomentoChange,
            onConfirmar = viewModel::confirmarConsumo,
            onDismiss = { viewModel.seleccionarAlimento(null) },
            snackbarHostState = snackbarHostState
        )
    }

    estado.planSeleccionado?.let { plan ->
        DialogoDetallePlan(
            plan = plan,
            onRegistrarItem = viewModel::registrarItemDePlan,
            onAplicarTodo = {
                viewModel.confirmarAplicarPlanCompleto()
                viewModel.seleccionarPlan(null)
            },
            onDismiss = { viewModel.seleccionarPlan(null) },
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
fun ConsumibleItem(alimento: Alimento, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = { Text(alimento.nombre, fontWeight = FontWeight.Bold) },
        supportingContent = {
            Text("${alimento.kcalPor100g.toInt()} kcal | P: ${alimento.proteinasPor100g.toInt()}g | HC: ${alimento.carbohidratosPor100g.toInt()}g | G: ${alimento.grasasPor100g.toInt()}g")
        },
        trailingContent = {
            if (alimento is Receta) {
                SuggestionChip(onClick = {}, label = { Text("Receta") })
            }
        }
    )
}

@Composable
fun DialogoDetallePlan(
    plan: Plan,
    onRegistrarItem: (ItemPlan) -> Unit,
    onAplicarTodo: () -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
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
            Box(contentAlignment = Alignment.Center) {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    Text(
                        text = if (plan.tipo == "SEMANAL") "Sugerencias para hoy (${hoy.dayOfWeek})" else "Contenido del plan",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (itemsAMostrar.isEmpty()) {
                        Text("No hay alimentos para hoy.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(itemsAMostrar) { item ->
                                ListItem(
                                    headlineContent = { Text(item.alimento.nombre, fontWeight = FontWeight.SemiBold) },
                                    supportingContent = { Text("${item.cantidadGramos.toInt()}g - ${item.momentoComida.name.lowercase()}") },
                                    trailingContent = {
                                        IconButton(onClick = { onRegistrarItem(item) }) {
                                            Icon(painter = painterResource(Res.drawable.ic_document_add), contentDescription = "Añadir", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                CustomSnackbarHost(snackbarHostState)
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
fun DialogoConfigurarConsumo(
    alimento: Alimento,
    cantidad: String,
    momento: MomentoComida,
    onCantidadChange: (String) -> Unit,
    onMomentoChange: (MomentoComida) -> Unit,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configurar Ingesta") },
        text = {
            Box(contentAlignment = Alignment.Center) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(alimento.nombre, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                    
                    OpenMiseTextField(
                        value = cantidad,
                        onValueChange = onCantidadChange,
                        label = "Cantidad (g)",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MomentoComida.entries.forEach { m ->
                            OpenMiseFilterChip(
                                selected = momento == m,
                                onClick = { onMomentoChange(m) },
                                label = m.name.lowercase().replaceFirstChar { it.uppercase() }
                            )
                        }
                    }

                    val gramos = cantidad.toDoubleOrNull() ?: 0.0
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("${alimento.calcularKcal(gramos).toInt()} kcal | ${alimento.calcularProteinas(gramos).toInt()}g Prot", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                CustomSnackbarHost(snackbarHostState)
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

@Composable
fun CustomSnackbarHost(hostState: SnackbarHostState, modifier: Modifier = Modifier) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data ->
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 1.0f),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Text(
                text = data.visuals.message,
                color = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
