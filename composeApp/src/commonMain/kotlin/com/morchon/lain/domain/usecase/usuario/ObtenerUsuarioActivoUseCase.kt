package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener el usuario que tiene la sesión activa actualmente.
 */
class ObtenerUsuarioActivoUseCase(private val repository: UsuarioRepository) {
    operator fun invoke(): Flow<Usuario?> = repository.obtenerUsuarioActivo()
}
