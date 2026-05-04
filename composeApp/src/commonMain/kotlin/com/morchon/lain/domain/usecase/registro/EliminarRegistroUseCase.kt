package com.morchon.lain.domain.usecase.registro

import com.morchon.lain.domain.repository.RegistroDiarioRepository

/**
 * Caso de uso para eliminar un registro de consumo del diario.
 */
class EliminarRegistroUseCase(
    private val registroRepository: RegistroDiarioRepository
) {
    suspend operator fun invoke(registroId: Long) {
        registroRepository.eliminarRegistro(registroId)
    }
}
