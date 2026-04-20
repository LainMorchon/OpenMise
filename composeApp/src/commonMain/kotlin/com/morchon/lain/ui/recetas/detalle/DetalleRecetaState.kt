package com.morchon.lain.ui.recetas.detalle

import com.morchon.lain.domain.model.Receta

data class DetalleRecetaState(
    val receta: Receta? = null,
    val estaCargando: Boolean = false,
    val error: String? = null,
    val mostrarConfirmacionBorrado: Boolean = false,
    val borradoExitoso: Boolean = false
)