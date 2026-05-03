package com.morchon.lain.domain.usecase.recetas

import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener la información detallada de una receta por su ID.
 */
class ObtenerDetalleRecetaUseCase(private val repository: RecetaRepository) {
    operator fun invoke(id: String): Flow<Receta?> = repository.obtenerRecetaCompleta(id)
}
