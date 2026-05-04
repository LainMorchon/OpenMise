package com.morchon.lain.domain.model

/**
 * Representa una plantilla de alimentación (Plan) que el usuario puede crear
 * y aplicar a su registro diario.
 */
data class Plan(
    val id: Long = 0,
    val usuarioId: String,
    val nombre: String,
    val descripcion: String? = null,
    val items: List<ItemPlan> = emptyList()
)
