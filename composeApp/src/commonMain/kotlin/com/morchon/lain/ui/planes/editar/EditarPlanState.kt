package com.morchon.lain.ui.planes.editar

import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.Plan

data class EditarPlanState(
    val plan: Plan = Plan(usuarioId = "", nombre = "", tipo = "DIA_UNICO"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val guardadoExitoso: Boolean = false,
    val showBuscadorAlimentos: Boolean = false,
    val indiceDiaSeleccionado: Int = 0, // 0 para DIA_UNICO, 1-7 para SEMANAL
    val listaPlanesDisponibles: List<Plan> = emptyList(),
    val showDialogoSeleccionarPlantilla: Boolean = false
)
