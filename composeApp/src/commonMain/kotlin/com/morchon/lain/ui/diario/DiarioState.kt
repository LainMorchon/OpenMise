package com.morchon.lain.ui.diario

import com.morchon.lain.domain.model.RegistroDiario
import kotlinx.datetime.LocalDate

data class DiarioState(
    val fechaSeleccionada: LocalDate,
    val registros: List<RegistroDiario> = emptyList(),
    val estaCargando: Boolean = false,
    val error: String? = null,
    val totalKcal: Double = 0.0,
    val totalProteinas: Double = 0.0,
    val totalCarbohidratos: Double = 0.0,
    val totalGrasas: Double = 0.0
)
