package com.morchon.lain.domain.usecase.consumo

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.repository.AlimentoRepository
import com.morchon.lain.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.first

/**
 * Caso de uso que permite buscar tanto alimentos en la API/Local
 * como recetas propias del usuario.
 */
class BuscarConsumiblesUseCase(
    private val alimentoRepository: AlimentoRepository,
    private val recetaRepository: RecetaRepository
) {
    suspend operator fun invoke(query: String): List<Alimento> {
        if (query.isBlank()) return emptyList()

        // 1. Buscamos alimentos en el repositorio (API + Cache Local)
        val alimentos = alimentoRepository.buscarAlimentos(query, "all")

        // 2. Buscamos recetas en el repositorio local
        // Filtramos manualmente por nombre ya que el repo nos da todas por ahora
        val todasLasRecetas = recetaRepository.obtenerTodasLasRecetas().first()
        val recetasFiltradas = todasLasRecetas.filter { 
            it.nombre.contains(query, ignoreCase = true) 
        }

        // Combinamos ambas listas (Receta hereda de Alimento)
        return recetasFiltradas + alimentos
    }
}
