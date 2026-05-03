package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener todos los usuarios registrados localmente.
 */
class ObtenerUsuariosUseCase(private val repository: UsuarioRepository) {
    operator fun invoke(): Flow<List<Usuario>> = repository.obtenerTodosLosUsuarios()
}
