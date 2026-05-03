package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.repository.UsuarioRepository

/**
 * Caso de uso para cerrar la sesión del usuario actual.
 */
class CerrarSesionUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke() = repository.cerrarSesion()
}
