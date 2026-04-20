package com.morchon.lain.ui.recetas.detalle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Receta
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRecetaScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: DetalleRecetaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.borradoExitoso) {
        if (state.borradoExitoso) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Receta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { state.receta?.id?.let { onNavigateToEdit(it) } }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { viewModel.onBorrarClick() }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(text = state.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            } else {
                state.receta?.let { receta ->
                    ContenidoReceta(receta)
                }
            }
        }
    }

    if (state.mostrarConfirmacionBorrado) {
        AlertDialog(
            onDismissRequest = { viewModel.onCancelarBorrado() },
            title = { Text("¿Borrar receta?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmarBorrado() }) {
                    Text("Borrar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onCancelarBorrado() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ContenidoReceta(receta: Receta) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(text = receta.nombre, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            MacroDetalle("Kcal", receta.kcalPor100g.toInt().toString())
            Spacer(modifier = Modifier.width(16.dp))
            MacroDetalle("Prot", "${receta.proteinasPor100g}g")
            Spacer(modifier = Modifier.width(16.dp))
            MacroDetalle("Carb", "${receta.carbohidratosPor100g}g")
            Spacer(modifier = Modifier.width(16.dp))
            MacroDetalle("Gras", "${receta.grasasPor100g}g")
        }

        if (receta.descripcion.isNotBlank()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Descripción", style = MaterialTheme.typography.titleLarge)
            Text(text = receta.descripcion, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Ingredientes", style = MaterialTheme.typography.titleLarge)
        receta.ingredientes.forEach { ingrediente ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "• ${ingrediente.alimento.nombre}", modifier = Modifier.weight(1f))
                Text(text = "${ingrediente.cantidadEnGramos}g", fontWeight = FontWeight.Bold)
            }
        }

        receta.pasosPreparacion?.let { pasos ->
            if (pasos.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Preparación", style = MaterialTheme.typography.titleLarge)
                Text(text = pasos, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun MacroDetalle(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}