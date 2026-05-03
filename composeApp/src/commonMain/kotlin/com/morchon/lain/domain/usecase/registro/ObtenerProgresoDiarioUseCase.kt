package com.morchon.lain.domain.usecase.registro

import com.morchon.lain.domain.model.ConsumoProgreso
import com.morchon.lain.domain.repository.RegistroDiarioRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Caso de uso que calcula el progreso nutricional del día actual de forma reactiva.
 */
class ObtenerProgresoDiarioUseCase(
    private val registroRepository: RegistroDiarioRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<ConsumoProgreso> {
        return usuarioRepository.obtenerUsuarioActivo().flatMapLatest { usuario ->
            if (usuario == null) return@flatMapLatest flowOf(ConsumoProgreso())

            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            registroRepository.obtenerRegistrosPorFecha(usuario.id, hoy).map { registros ->
                ConsumoProgreso(
                    kcalConsumidas = registros.sumOf { it.kcalHistoricas },
                    proteinasConsumidas = registros.sumOf { it.proteinasHistoricas },
                    carbohidratosConsumidos = registros.sumOf { it.carbohidratosHistoricos },
                    grasasConsumidas = registros.sumOf { it.grasasHistoricas },
                    kcalObjetivo = usuario.objetivos.kcal,
                    proteinasObjetivo = usuario.objetivos.proteinas,
                    carbohidratosObjetivo = usuario.objetivos.carbohidratos,
                    grasasObjetivo = usuario.objetivos.grasas
                )
            }
        }
    }
}
