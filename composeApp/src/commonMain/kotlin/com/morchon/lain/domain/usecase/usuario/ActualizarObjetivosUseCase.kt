package com.morchon.lain.domain.usecase.usuario

import com.morchon.lain.domain.model.ObjetivosNutricionales
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first

/**
 * Caso de uso para actualizar los objetivos nutricionales del usuario activo.
 */
class ActualizarObjetivosUseCase(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(nuevosObjetivos: ObjetivosNutricionales) {
        val usuarioActivo = repository.obtenerUsuarioActivo().first()
        usuarioActivo?.let { usuario ->
            val usuarioActualizado = usuario.copy(objetivos = nuevosObjetivos)
            repository.guardarUsuario(usuarioActualizado)
        }
    }
}
