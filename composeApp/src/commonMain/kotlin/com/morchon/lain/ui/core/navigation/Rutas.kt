package com.morchon.lain.ui.core.navigation
/**
 * Definición de todas las pantallas de la aplicación para evitar errores tipográficos.
 */
sealed class Rutas(val ruta: String) {
    data object Login: Rutas("login")
    data object Home: Rutas("home")
    data object Recetario : Rutas("recetario")
    data object CrearReceta : Rutas("crear_receta")
}