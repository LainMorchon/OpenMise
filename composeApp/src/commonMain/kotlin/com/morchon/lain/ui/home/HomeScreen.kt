package com.morchon.lain.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    alNavegarRecetario: () -> Unit,
    alNavegarCrearReceta: () -> Unit,
    alNavegarAPerfil: () -> Unit,
    alNavegarASeleccionarAlimento: () -> Unit,
    alNavegarAPlanes: () -> Unit,
    alCerrarSesion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    var mostrarMenu by remember { mutableStateOf(false) }

    LaunchedEffect(estado.sesionCerrada) {
        if (estado.sesionCerrada) {
            alCerrarSesion()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("OpenMise", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
                        }
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mis Planes") },
                                onClick = {
                                    mostrarMenu = false
                                    alNavegarAPlanes()
                                },
                                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Editar Perfil") },
                                onClick = { 
                                    mostrarMenu = false
                                    alNavegarAPerfil()
                                },
                                leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    mostrarMenu = false
                                    viewModel.cerrarSesion()
                                },
                                leadingIcon = { 
                                    Icon(
                                        Icons.AutoMirrored.Filled.ExitToApp, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    ) 
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = alNavegarASeleccionarAlimento,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Consumo")
            }
        },
        bottomBar = {
            BarraNavegacionInferior(
                rutaActual = "home",
                alPulsarHome = {},
                alPulsarRecetario = alNavegarRecetario,
                alPulsarCrear = { alNavegarCrearReceta() },
                alPulsarPlanes = alNavegarAPlanes
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
                text = "¡Hola, ${estado.usuario?.nombre ?: "Usuario"}! 👋",
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
                    
                    val progresoKcal = if (estado.progreso.kcalObjetivo > 0) {
                        (estado.progreso.kcalConsumidas / estado.progreso.kcalObjetivo).toFloat()
                    } else 0f
                    
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progresoKcal.coerceIn(0f, 1f) },
                            modifier = Modifier.size(120.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 10.dp
                        )
                        Text(
                            text = "${progresoKcal.times(100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${estado.progreso.kcalConsumidas.toInt()} / ${estado.progreso.kcalObjetivo} kcal",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta Secundaria: Macros
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Macronutrientes", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))

                    BarraMacro(
                        nombre = "Proteínas",
                        consumido = estado.progreso.proteinasConsumidas.toInt(),
                        total = estado.progreso.proteinasObjetivo,
                        color = Color(0xFFE57373)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BarraMacro(
                        nombre = "Carbohidratos",
                        consumido = estado.progreso.carbohidratosConsumidos.toInt(),
                        total = estado.progreso.carbohidratosObjetivo,
                        color = Color(0xFF64B5F6)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BarraMacro(
                        nombre = "Grasas",
                        consumido = estado.progreso.grasasConsumidas.toInt(),
                        total = estado.progreso.grasasObjetivo,
                        color = Color(0xFFFFD54F)
                    )
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
    alPulsarCrear: () -> Unit,
    alPulsarPlanes: () -> Unit
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
            selected = rutaActual == "planes",
            onClick = alPulsarPlanes,
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Planes") },
            label = { Text("Planes") }
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
        HomeScreen(
            alNavegarRecetario = {},
            alNavegarCrearReceta = {},
            alNavegarAPerfil = {},
            alNavegarASeleccionarAlimento = {},
            alNavegarAPlanes = {},
            alCerrarSesion = {}
        )
    }
}