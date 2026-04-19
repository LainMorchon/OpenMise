package com.morchon.lain.data.database.dao

import androidx.room.*
import com.morchon.lain.data.database.entity.AlimentoEntity
import com.morchon.lain.data.database.entity.DetalleRecetaEntity
import com.morchon.lain.data.database.entity.IngredienteRecetaEntity
import com.morchon.lain.data.database.model.RecetaCompleta
import kotlinx.coroutines.flow.Flow

@Dao
interface RecetaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarAlimento(alimento: AlimentoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarDetalle(detalle: DetalleRecetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarIngredientes(ingredientes: List<IngredienteRecetaEntity>)

    /**
     * Guarda una receta completa en las 3 tablas de forma atómica.
     */
    @Transaction
    suspend fun guardarRecetaCompleta(
        alimento: AlimentoEntity,
        detalle: DetalleRecetaEntity,
        ingredientes: List<IngredienteRecetaEntity>
    ) {
        insertarAlimento(alimento)
        insertarDetalle(detalle)
        insertarIngredientes(ingredientes)
    }

    /**
     * Obtiene el listado básico para el catálogo.
     */
    @Query("SELECT * FROM Alimento WHERE origen = 'CUSTOM_RECIPE'")
    fun obtenerTodasLasRecetas(): Flow<List<AlimentoEntity>>

    /**
     * Obtiene una receta con todo su detalle e ingredientes por ID.
     */
    @Transaction
    @Query("SELECT * FROM Alimento WHERE id = :recetaId")
    fun obtenerRecetaPorId(recetaId: String): Flow<RecetaCompleta?>

    @Query("DELETE FROM Alimento WHERE id = :recetaId")
    suspend fun eliminarReceta(recetaId: String)
}