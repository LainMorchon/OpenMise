package com.morchon.lain.ui.recetas.listado

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Alimento
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoRecetasScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCrear: () -> Unit,
    viewModel: ListadoRecetasViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Recetas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCrear) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Crear Receta")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.estaCargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.recetas.isEmpty()) {
                Text(
                    text = "Aún no has creado ninguna receta",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.recetas) { receta ->
                        RecetaItem(receta = receta)
                    }
                }
            }
        }
    }
}

@Composable
fun RecetaItem(receta: Alimento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = receta.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                MacroInfo(label = "Kcal", value = receta.kcalPor100g.toInt().toString())
                Spacer(modifier = Modifier.width(16.dp))
                MacroInfo(label = "Prot", value = "${receta.proteinasPor100g}g")
                Spacer(modifier = Modifier.width(16.dp))
                MacroInfo(label = "Carb", value = "${receta.carbohidratosPor100g}g")
                Spacer(modifier = Modifier.width(16.dp))
                MacroInfo(label = "Gras", value = "${receta.grasasPor100g}g")
            }
        }
    }
}

@Composable
fun MacroInfo(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}