package com.morchon.lain.domain.usecase.recetas

import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener todas las recetas disponibles.
 */
class ObtenerRecetasUseCase(private val repository: RecetaRepository) {
    operator fun invoke(): Flow<List<Receta>> = repository.obtenerTodasLasRecetas()
}
