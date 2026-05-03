package com.morchon.lain.ui.perfil

import com.morchon.lain.domain.model.Usuario

/**
 * Estado de la pantalla de perfil.
 */
data class PerfilState(
    val usuario: Usuario? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val exito: Boolean = false,
    // Campos temporales para la edición
    val kcal: String = "",
    val proteinas: String = "",
    val carbohidratos: String = "",
    val grasas: String = ""
)
