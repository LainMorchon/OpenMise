package com.morchon.lain.ui.home

import com.morchon.lain.domain.model.ConsumoProgreso
import com.morchon.lain.domain.model.Usuario

data class HomeState(
    val usuario: Usuario? = null,
    val progreso: ConsumoProgreso = ConsumoProgreso(),
    val sesionCerrada: Boolean = false
)