package com.morchon.lain.domain.model

/**
 * Entidad pura que representa a un Usuario en la aplicación.
 */

data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
   //TODO val objetivosNutricionales: ObjetivosNutricionales
    val estaLogeado: Boolean = true
)

