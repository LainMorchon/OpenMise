package com.morchon.lain.ui.recetas.listado

import com.morchon.lain.domain.model.Receta

data class ListadoRecetasState(
    val recetas: List<Receta> = emptyList(),
    val estaCargando: Boolean = false,
    val error: String? = null
)