package com.morchon.lain.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import com.morchon.lain.ui.core.components.OpenMiseLogo
import com.morchon.lain.ui.core.components.OpenMiseTextField
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.foundation.BorderStroke
import com.morchon.lain.ui.theme.OpenGreen
import com.morchon.lain.ui.theme.MiseOrange
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
    alNavegarAHistorial: () -> Unit,
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
                title = { 
                    OpenMiseLogo(
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        showIcon = true,
                        iconSize = 56.dp
                    )
                },
                actions = {
                    Box {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_hamburger_6),
                                contentDescription = "Opciones",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Historial de Consumo") },
                                onClick = {
                                    mostrarMenu = false
                                    alNavegarAHistorial()
                                },
                                leadingIcon = { Icon(painter = painterResource(Res.drawable.ic_history_2), contentDescription = null, modifier = Modifier.size(26.dp)) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Mis Planes") },
                                onClick = {
                                    mostrarMenu = false
                                    alNavegarAPlanes()
                                },
                                leadingIcon = { Icon(painter = painterResource(Res.drawable.ic_plan), contentDescription = null, modifier = Modifier.size(26.dp)) }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Editar Perfil") },
                                onClick = { 
                                    mostrarMenu = false
                                    alNavegarAPerfil()
                                },
                                leadingIcon = { Icon(painter = painterResource(Res.drawable.ic_settings), contentDescription = null, modifier = Modifier.size(26.dp)) }
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
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(26.dp)
                                    ) 
                                }
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Eliminar Cuenta", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    mostrarMenu = false
                                    viewModel.mostrarDialogoEliminar(true)
                                },
                                leadingIcon = { 
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_trash_bin_trash),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(26.dp)
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
                Icon(
                    painter = painterResource(Res.drawable.ic_document_add),
                    contentDescription = "Añadir Consumo",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        bottomBar = {
            BarraNavegacionInferior(
                rutaActual = "home",
                alPulsarHome = {},
                alPulsarRecetario = alNavegarRecetario,
                alPulsarCrear = { alNavegarCrearReceta() },
                alPulsarPlanes = alNavegarAPlanes,
                alPulsarDiario = alNavegarAHistorial
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
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = OpenGreen)) { append("¡") }
                    withStyle(style = SpanStyle(color = Color.White)) { append("Hola, ") }
                    withStyle(style = SpanStyle(color = MiseOrange)) { append(estado.usuario?.nombre ?: "Usuario") }
                    withStyle(style = SpanStyle(color = OpenGreen)) { append("!") }
                    append(" 👋")
                },
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu resumen de hoy",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = OpenGreen,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta Principal: Calorías
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, OpenGreen)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Calorías Consumidas", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    val progresoKcal = if (estado.progreso.kcalObjetivo > 0) {
                        (estado.progreso.kcalConsumidas / estado.progreso.kcalObjetivo).toFloat()
                    } else 0f
                    
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            progress = { progresoKcal.coerceIn(0f, 1f) },
                            modifier = Modifier.size(140.dp),
                            color = OpenGreen,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            strokeWidth = 12.dp
                        )
                        Text(
                            text = "${progresoKcal.times(100).toInt()}%",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${estado.progreso.kcalConsumidas.toInt()} / ${estado.progreso.kcalObjetivo} kcal ⚡",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tarjeta Secundaria: Macros
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(2.dp, MiseOrange)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Macronutrientes", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(16.dp))

                    BarraMacro(
                        nombre = "Proteínas 🍗",
                        consumido = estado.progreso.proteinasConsumidas.toInt(),
                        total = estado.progreso.proteinasObjetivo,
                        color = Color(0xFFE57373)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BarraMacro(
                        nombre = "Carbohidratos 🍝",
                        consumido = estado.progreso.carbohidratosConsumidos.toInt(),
                        total = estado.progreso.carbohidratosObjetivo,
                        color = Color(0xFF64B5F6)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    BarraMacro(
                        nombre = "Grasas 🥑",
                        consumido = estado.progreso.grasasConsumidas.toInt(),
                        total = estado.progreso.grasasObjetivo,
                        color = Color(0xFFFFD54F)
                    )
                }
            }
        }
    }

    if (estado.mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = { viewModel.mostrarDialogoEliminar(false) },
            title = { Text("¿Eliminar cuenta definitivamente?") },
            text = {
                Column {
                    Text("Esta acción no se puede deshacer. Por favor, introduce tu contraseña para confirmar.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OpenMiseTextField(
                        value = estado.contrasenaConfirmacion,
                        onValueChange = { viewModel.alCambiarContrasenaConfirmacion(it) },
                        label = "Contraseña",
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (estado.errorEliminar != null) {
                        Text(
                            text = estado.errorEliminar!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.eliminarCuenta() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar definitivamente")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.mostrarDialogoEliminar(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun BarraMacro(nombre: String, consumido: Int, total: Int, color: Color) {
    val progreso = (if (total > 0) consumido.toFloat() / total.toFloat() else 0f).coerceIn(0f, 1f)
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(nombre, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            Text("${consumido}g / ${total}g", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
    alPulsarPlanes: () -> Unit,
    alPulsarDiario: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == "home",
            onClick = alPulsarHome,
            icon = { Icon(painter = painterResource(Res.drawable.ic_home_alt), contentDescription = "Inicio", modifier = Modifier.size(26.dp)) },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = rutaActual == "diario",
            onClick = alPulsarDiario,
            icon = { Icon(painter = painterResource(Res.drawable.ic_history_2), contentDescription = "Diario", modifier = Modifier.size(26.dp)) },
            label = { Text("Diario") }
        )
        NavigationBarItem(
            selected = rutaActual == "planes",
            onClick = alPulsarPlanes,
            icon = { Icon(painter = painterResource(Res.drawable.ic_plan), contentDescription = "Planes", modifier = Modifier.size(26.dp)) },
            label = { Text("Planes") }
        )
        NavigationBarItem(
            selected = rutaActual == "recetario",
            onClick = alPulsarRecetario,
            icon = { Icon(painter = painterResource(Res.drawable.ic_chef_hat), contentDescription = "Recetario", modifier = Modifier.size(26.dp)) },
            label = { Text("Recetas") }
        )
    }
}
