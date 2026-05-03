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
        // 1. Buscamos recetas (Siempre mostramos las locales si query es vacío)
        val todasLasRecetas = recetaRepository.obtenerTodasLasRecetas().first()
        val recetasFiltradas = if (query.isBlank()) {
            todasLasRecetas
        } else {
            todasLasRecetas.filter { it.nombre.contains(query, ignoreCase = true) }
        }

        // 2. Buscamos alimentos
        val alimentos = if (query.isBlank()) {
            // Si no hay query, mostramos los que ya tenemos guardados localmente (historial/caché)
            alimentoRepository.obtenerAlimentosLocales()
        } else {
            alimentoRepository.buscarAlimentos(query, "all")
        }

        return recetasFiltradas + alimentos
    }
}
