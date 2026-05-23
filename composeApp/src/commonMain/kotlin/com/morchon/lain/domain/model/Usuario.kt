package com.morchon.lain.domain.model

/**
 * Entidad pura que representa a un Usuario en la aplicación.
 */

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val contrasena: String, // Añadido campo contraseña
    val objetivos: ObjetivosNutricionales = ObjetivosNutricionales(),
    val estaLogeado: Boolean = true
)

