package com.morchon.lain.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    alNavegarRecetario: () -> Unit,
    alNavegarCrearReceta: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                rutaActual = "home",
                alPulsarHome = {}, // Ya estamos aquí
                alPulsarRecetario = alNavegarRecetario,
                alPulsarCrear = alNavegarCrearReceta
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "¡Hola, Lain! 👋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Tu resumen de hoy",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Principal: Calorías
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Calorías Consumidas", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Simulamos un anillo de progreso (50%)
                    CircularProgressIndicator(
                        progress = { 0.5f },
                        modifier = Modifier.size(100.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 8.dp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1000 / 2000 kcal", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta Secundaria: Macros
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Macronutrientes", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))

                    BarraMacro("Proteínas", 80, 150, Color(0xFFE57373))
                    Spacer(modifier = Modifier.height(8.dp))
                    BarraMacro("Carbohidratos", 120, 200, Color(0xFF64B5F6))
                    Spacer(modifier = Modifier.height(8.dp))
                    BarraMacro("Grasas", 40, 65, Color(0xFFFFD54F))
                }
            }
        }
    }
}

@Composable
fun BarraMacro(nombre: String, consumido: Int, total: Int, color: Color) {
    val progreso = consumido.toFloat() / total.toFloat()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, fontSize = 14.sp)
            Text("${consumido}g / ${total}g", fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color
        )
    }
}

@Composable
fun BarraNavegacionInferior(
    rutaActual: String,
    alPulsarHome: () -> Unit,
    alPulsarRecetario: () -> Unit,
    alPulsarCrear: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == "home",
            onClick = alPulsarHome,
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = rutaActual == "recetario",
            onClick = alPulsarRecetario,
            icon = { Icon(Icons.Default.List, contentDescription = "Recetario") },
            label = { Text("Recetario") }
        )
        NavigationBarItem(
            selected = rutaActual == "crear_receta",
            onClick = alPulsarCrear,
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Crear") },
            label = { Text("Crear") }
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        HomeScreen({}, {})
    }
}