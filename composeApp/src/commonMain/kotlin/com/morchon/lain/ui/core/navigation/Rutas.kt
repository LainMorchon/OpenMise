package com.morchon.lain.ui.core.navigation
/**
 * Definición de todas las pantallas de la aplicación para evitar errores tipográficos.
 */
sealed class Rutas(val ruta: String) {
    data object Login: Rutas("login")
    data object Home: Rutas("home")
    data object Recetario : Rutas("recetario")
    data object CrearReceta : Rutas("crear_receta/{recetaId}?") {
        fun crearRuta(recetaId: String? = null) = if (recetaId != null) "crear_receta/$recetaId" else "crear_receta/null"
    }
    data object DetalleReceta : Rutas("detalle_receta/{recetaId}") {
        fun crearRuta(recetaId: String) = "detalle_receta/$recetaId"
    }
    data object Registro : Rutas("registro")
    data object Perfil : Rutas("perfil")
}