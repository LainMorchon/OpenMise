package com.morchon.lain.domain.model

/**
 * Representa un alimento específico con una cantidad determinada dentro de una receta.
 */
data class Ingrediente(
    val alimento: Alimento,
    val cantidadEnGramos: Float
) {
    // Helpers de negocio: Calculan los macros reales basados en el peso automáticamente
    val kcalTotales: Float
        get() = (alimento.kcalPor100g * cantidadEnGramos) / 100f

    val proteinasTotales: Float
        get() = (alimento.proteinasPor100g * cantidadEnGramos) / 100f

    val carbohidratosTotales: Float
        get() = (alimento.carbohidratosPor100g * cantidadEnGramos) / 100f

    val grasasTotales: Float
        get() = (alimento.grasasPor100g * cantidadEnGramos) / 100f
}