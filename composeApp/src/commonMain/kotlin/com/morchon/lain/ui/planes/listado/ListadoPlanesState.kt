package com.morchon.lain.ui.planes.listado

import com.morchon.lain.domain.model.Plan

data class ListadoPlanesState(
    val planes: List<Plan> = emptyList(),
    val estaCargando: Boolean = false,
    val error: String? = null,
    val planAEliminar: Plan? = null,
    val mostrarDialogoEliminar: Boolean = false
)
