package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlin.random.Random

/**
 * Caso de uso para registrar un nuevo usuario en la aplicación.
 */
class RegistrarUsuarioUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(nombre: String, email: String): Result<Unit> {
        if (nombre.isBlank() || email.isBlank()) {
            return Result.failure(Exception("Nombre y email son obligatorios"))
        }

        val nuevoUsuario = Usuario(
            id = "usr_${Random.nextInt(1000, 9999)}",
            nombre = nombre,
            email = email,
            estaLogeado = false
        )

        return try {
            repository.guardarUsuario(nuevoUsuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
