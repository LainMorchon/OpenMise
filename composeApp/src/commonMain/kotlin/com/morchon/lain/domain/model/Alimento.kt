package com.morchon.lain.domain.model

/**
 * Entidad base del dominio para cualquier elemento consumible.
 */
open class Alimento(
    val id: String,
    val nombre: String,
    val origen: String, // "API_RAW", "API_COMMERCIAL", "CUSTOM_RECIPE"
    val kcalPor100g: Float,
    val proteinasPor100g: Float,
    val carbohidratosPor100g: Float,
    val grasasPor100g: Float
)