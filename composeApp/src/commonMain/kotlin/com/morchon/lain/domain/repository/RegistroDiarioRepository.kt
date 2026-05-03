package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.RegistroDiario
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Contrato para la gestión del log de consumo diario.
 */
interface RegistroDiarioRepository {
    suspend fun guardarRegistro(registro: RegistroDiario)
    fun obtenerRegistrosPorFecha(usuarioId: String, fecha: LocalDate): Flow<List<RegistroDiario>>
    suspend fun eliminarRegistro(id: Long)
}
