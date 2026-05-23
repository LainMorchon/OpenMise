package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import com.morchon.lain.domain.security.PasswordCipher
import kotlin.random.Random

/**
 * Caso de uso para registrar un nuevo usuario en la aplicación.
 */
class RegistrarUsuarioUseCase(
    private val repository: UsuarioRepository,
    private val passwordCipher: PasswordCipher
) {
    suspend operator fun invoke(nombre: String, email: String, contrasena: String): Result<Unit> {
        if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
            return Result.failure(Exception("Todos los campos son obligatorios"))
        }

        if (!esEmailValido(email)) {
            return Result.failure(Exception("El formato del email no es válido"))
        }

        if (contrasena.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }

        val usuarioExistente = repository.obtenerUsuarioPorEmail(email)
        if (usuarioExistente != null) {
            return Result.failure(Exception("El email ya está registrado"))
        }

        val nuevoUsuario = Usuario(
            id = "usr_${Random.nextInt(1000, 9999)}",
            nombre = nombre,
            email = email,
            contrasena = passwordCipher.hash(contrasena),
            estaLogeado = false
        )

        return try {
            repository.guardarUsuario(nuevoUsuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun esEmailValido(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}
