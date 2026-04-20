package com.morchon.lain.ui.recetas.crear

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Alimento // Asegúrate de que el import sea correcto
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearRecetaScreen(
    onNavigateBack: () -> Unit,
    viewModel: CrearRecetaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // --- LÓGICA DE ESTADO PARA EL DIÁLOGO ---
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Si el guardado ha sido un éxito, volvemos atrás automáticamente
    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            onNavigateBack()
        }
    }

    // El diálogo se coloca fuera del Scaffold o dentro, pero NO dentro de LazyColumn directamente
    if (mostrarDialogo) {
        SeleccionarIngredienteDialog(
            onDismiss = { mostrarDialogo = false },
            onConfirm = { alimento, gramos ->
                viewModel.anadirIngrediente(alimento, gramos)
                mostrarDialogo = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.nombre.isBlank()) "Nueva Receta" else state.nombre) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.guardarReceta() },
                        enabled = state.nombre.isNotBlank() && !state.estaGuardando
                    ) {
                        if (state.estaGuardando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Campo Nombre
            item {
                OutlinedTextField(
                    value = state.nombre,
                    onValueChange = { viewModel.onNombreCambiado(it) },
                    label = { Text("Nombre de la receta") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // 2. Campo Descripción
            item {
                OutlinedTextField(
                    value = state.descripcion,
                    onValueChange = { viewModel.onDescripcionCambiada(it) },
                    label = { Text("Descripción / Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            // 3. Resumen de Macros
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Kcal: ${state.kcalTotales.toInt()}")
                        Text("P: ${state.proteinasTotales.toInt()}g")
                        Text("HC: ${state.carbohidratosTotales.toInt()}g")
                        Text("G: ${state.grasasTotales.toInt()}g")
                    }
                }
            }

            // 4. Cabecera de Sección de Ingredientes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { mostrarDialogo = true }) {
                        Text("+ Añadir")
                    }
                }
            }

            // 5. Lista dinámica de ingredientes añadidos
            items(state.ingredientesAñadidos.size) { index ->
                val ingrediente = state.ingredientesAñadidos[index]
                ListItem(
                    headlineContent = { Text(ingrediente.alimento.nombre) },
                    supportingContent = { Text("${ingrediente.cantidadEnGramos}g - ${ingrediente.kcalTotales.toInt()} kcal") },
                    trailingContent = {
                        IconButton(onClick = { viewModel.eliminarIngrediente(ingrediente) }) {
                            Text("❌")
                        }
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun SeleccionarIngredienteDialog(
    onDismiss: () -> Unit,
    onConfirm: (Alimento, Float) -> Unit
) {
    // --- AVISO: LISTA PROVISIONAL ---
    // TODO: En el futuro, estos alimentos vendrán de una búsqueda real en la base de datos o API.
    val alimentosDePrueba = listOf(
        Alimento("1", "Pollo (Pechuga)", "API_RAW", 165f, 31f, 0f, 3.6f),
        Alimento("2", "Arroz Blanco", "API_RAW", 130f, 2.7f, 28f, 0.3f),
        Alimento("3", "Aceite de Oliva", "API_RAW", 884f, 0f, 0f, 100f),
        Alimento("4", "Huevo", "API_RAW", 155f, 13f, 1.1f, 11f)
    )

    var busqueda by remember { mutableStateOf("") }
    var gramos by remember { mutableStateOf("100") }
    var alimentoSeleccionado by remember { mutableStateOf<Alimento?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir ingrediente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar alimento...") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Filtro provisional sobre la lista hardcoded
                val filtrados = alimentosDePrueba.filter { it.nombre.contains(busqueda, ignoreCase = true) }

                LazyColumn(modifier = Modifier.height(150.dp)) {
                    items(filtrados.size) { index ->
                        val alimento = filtrados[index]
                        val esSeleccionado = alimento == alimentoSeleccionado

                        Surface(
                            onClick = { alimentoSeleccionado = alimento },
                            color = if (esSeleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text(alimento.nombre, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                OutlinedTextField(
                    value = gramos,
                    onValueChange = { gramos = it },
                    label = { Text("Cantidad en gramos") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val g = gramos.toFloatOrNull() ?: 0f
                    if (alimentoSeleccionado != null && g > 0) {
                        onConfirm(alimentoSeleccionado!!, g)
                    }
                },
                enabled = alimentoSeleccionado != null
            ) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}