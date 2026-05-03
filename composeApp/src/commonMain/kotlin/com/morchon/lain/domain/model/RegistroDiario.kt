package com.morchon.lain.domain.model

import kotlinx.datetime.LocalDate

/**
 * Representa una entrada en el log de consumo diario.
 * Sigue el patrón Snapshot: guarda los macros en el momento del consumo.
 */
data class RegistroDiario(
    val id: Long = 0,
    val usuarioId: String,
    val alimentoId: String, // Referencia al alimento original
    val nombreAlimento: String,
    val fecha: LocalDate,
    val cantidadGramos: Double,
    val momentoComida: MomentoComida,
    // Macros "congelados" (Snapshot)
    val kcalHistoricas: Double,
    val proteinasHistoricas: Double,
    val carbohidratosHistoricos: Double,
    val grasasHistoricas: Double
)

enum class MomentoComida {
    DESAYUNO, ALMUERZO, CENA, SNACK
}
