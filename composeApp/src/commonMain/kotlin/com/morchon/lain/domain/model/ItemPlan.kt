package com.morchon.lain.domain.model

/**
 * Representa un elemento dentro de un plan nutricional (plantilla).
 */
data class ItemPlan(
    val id: Long = 0,
    val alimento: Alimento,
    val cantidadGramos: Double,
    val momentoComida: MomentoComida,
    val indiceDia: Int // 0 para planes de un día, 1-7 para planes semanales, etc.
)
