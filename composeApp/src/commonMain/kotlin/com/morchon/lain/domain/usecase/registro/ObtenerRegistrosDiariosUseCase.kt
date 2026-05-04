package com.morchon.lain.domain.usecase.registro

import com.morchon.lain.domain.model.RegistroDiario
import com.morchon.lain.domain.repository.RegistroDiarioRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate

/**
 * Caso de uso para obtener la lista de consumos de un usuario en una fecha concreta.
 */
class ObtenerRegistrosDiariosUseCase(
    private val registroRepository: RegistroDiarioRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(fecha: LocalDate): Flow<List<RegistroDiario>> {
        return usuarioRepository.obtenerUsuarioActivo().flatMapLatest { usuario ->
            if (usuario == null) return@flatMapLatest flowOf(emptyList())
            
            registroRepository.obtenerRegistrosPorFecha(usuario.id, fecha)
        }
    }
}
