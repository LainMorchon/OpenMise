package com.morchon.lain.domain.usecase.alimentos

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.repository.AlimentoRepository

/**
 * Caso de uso para buscar alimentos en el catálogo (API o local).
 */
class BuscarAlimentosUseCase(private val repository: AlimentoRepository) {
    suspend operator fun invoke(query: String, tipo: String): List<Alimento> {
        return repository.buscarAlimentos(query, tipo)
    }
}
