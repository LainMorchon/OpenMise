package com.morchon.lain.domain.model

/**
 * Representa el resumen del consumo diario frente a los objetivos.
 */
data class ConsumoProgreso(
    val kcalConsumidas: Double = 0.0,
    val proteinasConsumidas: Double = 0.0,
    val carbohidratosConsumidos: Double = 0.0,
    val grasasConsumidas: Double = 0.0,
    val kcalObjetivo: Int = 2000,
    val proteinasObjetivo: Int = 150,
    val carbohidratosObjetivo: Int = 200,
    val grasasObjetivo: Int = 65
)
