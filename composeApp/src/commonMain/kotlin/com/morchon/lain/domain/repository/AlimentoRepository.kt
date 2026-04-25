package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Alimento

/**
 * Interfaz que define las operaciones permitidas sobre los alimentos.
 */
interface AlimentoRepository {
    /**
     * Busca alimentos por una cadena de texto y un tipo de filtro.
     * @param query Texto a buscar.
     * @param type Tipo de alimento: "all", "generic", "brand".
     */
    suspend fun buscarAlimentos(query: String, type: String = "all"): List<Alimento>

    suspend fun guardarAlimentoLocal(alimento: Alimento)
}
