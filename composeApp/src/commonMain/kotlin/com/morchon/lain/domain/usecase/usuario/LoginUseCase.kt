package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.repository.UsuarioRepository

/**
 * Caso de uso para gestionar el inicio de sesión de un usuario.
 * Verifica la existencia del usuario y lo marca como activo en el sistema.
 */
class LoginUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(email: String): Boolean {
        if (email.isBlank()) return false
        
        val usuario = repository.obtenerUsuarioPorEmail(email)
        return if (usuario != null) {
            repository.setUsuarioActivo(usuario.id)
            true
        } else {
            false
        }
    }
}
