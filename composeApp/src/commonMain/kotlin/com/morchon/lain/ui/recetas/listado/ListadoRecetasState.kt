package com.morchon.lain.ui.recetas.listado

import com.morchon.lain.domain.model.Alimento

data class ListadoRecetasState(
    val recetas: List<Alimento> = emptyList(),
    val estaCargando: Boolean = false,
    val error: String? = null
)