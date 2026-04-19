package com.morchon.lain

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.morchon.lain.ui.core.navigation.Rutas
import com.morchon.lain.ui.home.HomeScreen
import com.morchon.lain.ui.login.LoginScreen
import com.morchon.lain.ui.recetas.crear.CrearRecetaScreen
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
                            // Viajamos al Home y destruimos el Login para no poder volver atrás con el botón "Back"
                            navController.navigate(Rutas.Home.ruta) {
                                popUpTo(Rutas.Login.ruta) { inclusive = true }
                            }
                        }
                    )
                }

                // PANTALLA 2: HOME
                composable(Rutas.Home.ruta) {
                    HomeScreen(
                        alNavegarRecetario = { navController.navigate(Rutas.Recetario.ruta) },
                        alNavegarCrearReceta = { navController.navigate(Rutas.CrearReceta.ruta) }
                    )
                }

                // PANTALLA 3: RECETARIO (Próximamente)
                composable(Rutas.Recetario.ruta) {
                    // Aquí irá la RecetaListScreen
                }

                // PANTALLA 4: CREAR RECETA (Próximamente)
                composable(Rutas.CrearReceta.ruta) {
                    CrearRecetaScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}