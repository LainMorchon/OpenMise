package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Alimento

/**
 * Interfaz que define las operaciones permitidas sobre los alimentos.
 * Siguiendo Clean Architecture, el dominio no sabe de dónde vienen los datos (API o DB).
 */
interface AlimentoRepository {
    /**
     * Busca alimentos por una cadena de texto.
     * @return Una lista de alimentos listos para ser usados en la UI.
     */
    suspend fun buscarAlimentos(query: String): List<Alimento>

    /**
     * Guarda un alimento en la base de datos local para que sea accesible sin conexión
     * o para referenciarlo en recetas locales.
     */
    suspend fun guardarAlimentoLocal(alimento: Alimento)
}
