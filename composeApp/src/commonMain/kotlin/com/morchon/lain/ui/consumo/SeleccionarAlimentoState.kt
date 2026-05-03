package com.morchon.lain.ui.consumo

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.MomentoComida

/**
 * Estado para la pantalla de selección de alimentos/recetas.
 */
data class SeleccionarAlimentoState(
    val query: String = "",
    val listaResultados: List<Alimento> = emptyList(),
    val cargando: Boolean = false,
    val alimentoSeleccionado: Alimento? = null,
    val cantidadGramos: String = "100",
    val momentoComida: MomentoComida = MomentoComida.DESAYUNO,
    val guardadoExitoso: Boolean = false
)
