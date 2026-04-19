package com.morchon.lain.data.repository

import com.morchon.lain.data.database.dao.RecetaDao
import com.morchon.lain.data.mapper.aAlimentoEntity
import com.morchon.lain.data.mapper.aDetalleEntity
import com.morchon.lain.data.mapper.aDominio
import com.morchon.lain.data.mapper.aIngredientesEntities
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecetaRepositoryImpl(
    private val recetaDao: RecetaDao
) : RecetaRepository {

    override suspend fun guardarReceta(receta: Receta) {
        // Usamos los mappers para convertir el objeto de dominio en entidades de Room
        val alimentoBase = receta.aAlimentoEntity()
        val detalle = receta.aDetalleEntity()

        // 1. Extraemos los AlimentoEntity de cada ingrediente para asegurar integridad referencial
        val ingredientesAlimentos = receta.ingredientes.map { it.alimento.aAlimentoEntity() }

        // 2. Mapeamos las relaciones de la tabla intermedia
        val relaciones = receta.aIngredientesEntities()

        // Llamamos a la transacción atómica del DAO con todos los datos necesarios
        recetaDao.guardarRecetaCompleta(
            alimento = alimentoBase,
            detalle = detalle,
            ingredientesAlimentos = ingredientesAlimentos,
            relaciones = relaciones
        )
    }

    override fun obtenerTodasLasRecetas(): Flow<List<Alimento>> {
        return recetaDao.obtenerTodasLasRecetas().map { lista ->
            lista.map { it.aDominio() }
        }
    }

    override fun obtenerRecetaCompleta(id: String): Flow<Receta?> {
        return recetaDao.obtenerRecetaPorId(id).map { it?.aDominio() }
    }

    override suspend fun eliminarReceta(id: String) {
        recetaDao.eliminarReceta(id)
    }
}