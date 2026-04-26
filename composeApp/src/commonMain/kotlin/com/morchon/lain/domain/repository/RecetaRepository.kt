package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Receta
import kotlinx.coroutines.flow.Flow

interface RecetaRepository {
    suspend fun guardarReceta(receta: Receta)
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>
    fun obtenerRecetaCompleta(id: String): Flow<Receta?>
    suspend fun eliminarReceta(id: String)
}