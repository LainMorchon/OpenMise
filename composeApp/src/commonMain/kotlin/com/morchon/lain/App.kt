package com.morchon.lain

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.morchon.lain.ui.core.navigation.Rutas
import com.morchon.lain.ui.home.HomeScreen
import com.morchon.lain.ui.home.HomeViewModel
import com.morchon.lain.ui.login.LoginScreen
import com.morchon.lain.ui.registro.RegistroScreen
import com.morchon.lain.ui.recetas.crear.CrearRecetaScreen
import com.morchon.lain.ui.recetas.detalle.DetalleRecetaScreen
import com.morchon.lain.ui.recetas.listado.ListadoRecetasScreen
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
                        alNavegarCrearReceta = { navController.navigate(Rutas.CrearReceta.ruta) },
                        alCerrarSesion = {
                            navController.navigate(Rutas.Login.ruta) {
                                popUpTo(Rutas.Home.ruta) { inclusive = true }
                            }
                        }
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
                composable(Rutas.CrearReceta.ruta) {
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
            }
        }
    }
}