package com.morchon.lain.domain.model

/**
 * Representa una plantilla de alimentación (Plan) que el usuario puede crear
 * y aplicar a su registro diario.
 */
data class Plan(
    val id: Long = 0,
    val usuarioId: String,
    val nombre: String,
    val tipo: String, // "DIA_UNICO", "SEMANAL", etc.
    val items: List<ItemPlan> = emptyList()
)
