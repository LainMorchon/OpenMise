package com.morchon.lain.domain.usecase.alimentos

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.repository.AlimentoRepository

/**
 * Caso de uso para persistir un alimento en la base de datos local.
 */
class GuardarAlimentoLocalUseCase(private val repository: AlimentoRepository) {
    suspend operator fun invoke(alimento: Alimento) {
        repository.guardarAlimentoLocal(alimento)
    }
}
