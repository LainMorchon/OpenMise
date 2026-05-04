package com.morchon.lain

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.morchon.lain.ui.core.navigation.Rutas
import com.morchon.lain.ui.home.HomeScreen
import com.morchon.lain.ui.home.HomeViewModel
import com.morchon.lain.ui.login.LoginScreen
import com.morchon.lain.ui.consumo.SeleccionarAlimentoScreen
import com.morchon.lain.ui.perfil.PerfilScreen
import com.morchon.lain.ui.perfil.PerfilViewModel
import com.morchon.lain.ui.registro.RegistroScreen
import com.morchon.lain.ui.recetas.crear.CrearRecetaScreen
import com.morchon.lain.ui.recetas.detalle.DetalleRecetaScreen
import com.morchon.lain.ui.recetas.listado.ListadoRecetasScreen
import com.morchon.lain.ui.planes.listado.ListadoPlanesScreen
import com.morchon.lain.ui.planes.editar.EditarPlanScreen
import com.morchon.lain.ui.login.LoginViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        KoinContext {
            // El controlador que maneja los viajes entre pantallas
            val navController = rememberNavController()

            // Definimos el mapa de pantallas. Empezamos en el Login.
            NavHost(
                navController = navController,
                startDestination = Rutas.Login.ruta
            ) {

                // PANTALLA 1: LOGIN
                composable(Rutas.Login.ruta) {
                    val loginViewModel = koinViewModel<LoginViewModel>()
                    LoginScreen(
                        viewModel = loginViewModel,
                        alNavegarAlHome = {
                            navController.navigate(Rutas.Home.ruta) {
                                popUpTo(Rutas.Login.ruta) { inclusive = true }
                            }
                        },
                        alNavegarAlRegistro = {
                            navController.navigate(Rutas.Registro.ruta)
                        }
                    )
                }

                // PANTALLA DE REGISTRO
                composable(Rutas.Registro.ruta) {
                    RegistroScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // PANTALLA 2: HOME
                composable(Rutas.Home.ruta) {
                    val homeViewModel = koinViewModel<HomeViewModel>()
                    HomeScreen(
                        viewModel = homeViewModel,
                        alNavegarRecetario = { navController.navigate(Rutas.Recetario.ruta) },
                        alNavegarCrearReceta = { navController.navigate(Rutas.CrearReceta.crearRuta()) },
                        alNavegarAPerfil = { navController.navigate(Rutas.Perfil.ruta) },
                        alNavegarASeleccionarAlimento = { navController.navigate(Rutas.SeleccionarAlimento.ruta) },
                        alNavegarAPlanes = { navController.navigate(Rutas.Planes.ruta) },
                        alCerrarSesion = {
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(Rutas.Home.ruta) { inclusive = true }
                            }
                        }
                    )
                }

                // PANTALLA SELECCIONAR ALIMENTO
                composable(Rutas.SeleccionarAlimento.ruta) {
                    SeleccionarAlimentoScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // PANTALLA PERFIL
                composable(Rutas.Perfil.ruta) {
                    val perfilViewModel = koinViewModel<PerfilViewModel>()
                    PerfilScreen(
                        viewModel = perfilViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // PANTALLA 3: RECETARIO
                composable(Rutas.Recetario.ruta) {
                    ListadoRecetasScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCrear = { navController.navigate(Rutas.CrearReceta.crearRuta()) },
                        onNavigateToDetalle = { id -> 
                            navController.navigate(Rutas.DetalleReceta.crearRuta(id)) 
                        }
                    )
                }

                // PANTALLA 4: CREAR RECETA
                composable(
                    route = Rutas.CrearReceta.ruta,
                    arguments = listOf(
                        androidx.navigation.navArgument("recetaId") {
                            type = androidx.navigation.NavType.StringType
                            nullable = true
                            defaultValue = "null"
                        }
                    )
                ) {
                    CrearRecetaScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // PANTALLA 5: DETALLE RECETA
                composable(Rutas.DetalleReceta.ruta) {
                    DetalleRecetaScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToEdit = { id ->
                            navController.navigate(Rutas.CrearReceta.crearRuta(id))
                        }
                    )
                }

                // PANTALLA 6: LISTADO DE PLANES
                composable(Rutas.Planes.ruta) {
                    ListadoPlanesScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToCrear = { navController.navigate(Rutas.EditarPlan.crearRuta()) },
                        onNavigateToEditar = { id ->
                            navController.navigate(Rutas.EditarPlan.crearRuta(id))
                        }
                    )
                }

                // PANTALLA 7: CREAR/EDITAR PLAN
                composable(
                    route = Rutas.EditarPlan.ruta,
                    arguments = listOf(
                        androidx.navigation.navArgument("planId") {
                            type = androidx.navigation.NavType.StringType
                            nullable = true
                            defaultValue = "null"
                        }
                    )
                ) {
                    EditarPlanScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}