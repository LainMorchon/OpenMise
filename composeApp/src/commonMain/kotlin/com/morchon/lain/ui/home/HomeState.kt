package com.morchon.lain.ui.home

import com.morchon.lain.domain.model.Usuario

data class HomeState(
    val usuario: Usuario? = null,
    val sesionCerrada: Boolean = false
)