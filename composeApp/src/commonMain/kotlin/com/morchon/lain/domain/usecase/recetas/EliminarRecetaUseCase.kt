package com.morchon.lain.domain.usecase.recetas

import com.morchon.lain.domain.repository.RecetaRepository

/**
 * Caso de uso para eliminar una receta del sistema.
 */
class EliminarRecetaUseCase(private val repository: RecetaRepository) {
    suspend operator fun invoke(id: String) = repository.eliminarReceta(id)
}
