package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlin.random.Random

/**
 * Caso de uso para registrar un nuevo usuario en la aplicación.
 */
class RegistrarUsuarioUseCase(private val repository: UsuarioRepository) {
    suspend operator fun invoke(nombre: String, email: String, contrasena: String): Result<Unit> {
        if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        val usuarioExistente = repository.obtenerUsuarioPorEmail(email)
        if (usuarioExistente != null) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        val nuevoUsuario = Usuario(
            id = "usr_${Random.nextInt(1000, 9999)}",
            nombre = nombre,
            email = email,
            contrasena = contrasena,
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
