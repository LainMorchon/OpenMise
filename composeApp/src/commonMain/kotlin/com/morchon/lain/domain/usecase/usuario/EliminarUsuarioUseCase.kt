package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.repository.UsuarioRepository

/**
 * Caso de uso para eliminar un usuario del sistema previa verificación de contraseña.
 */
class EliminarUsuarioUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(usuarioId: String, contrasena: String): Result<Unit> {
        return try {
            val usuario = repository.obtenerUsuarioPorId(usuarioId) 
                ?: return Result.failure(Exception("Usuario no encontrado"))
            
            if (usuario.contrasena != contrasena) {
                return Result.failure(Exception("La contraseña es incorrecta"))
            }

            repository.eliminarUsuario(usuario.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
