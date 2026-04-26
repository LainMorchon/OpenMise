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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarAlimentos(alimentos: List<AlimentoEntity>)

    /**
     * Guarda una receta completa en las tablas de forma atómica.
     * 1. Inserta los alimentos que componen los ingredientes (si no existen).
     * 2. Inserta la cabecera de la receta.
     * 3. Inserta el detalle de la receta.
     * 4. Inserta las relaciones de los ingredientes.
     */
    @Transaction
    suspend fun guardarRecetaCompleta(
        alimento: AlimentoEntity,
        detalle: DetalleRecetaEntity,
        ingredientesAlimentos: List<AlimentoEntity>,
        relaciones: List<IngredienteRecetaEntity>
    ) {
        insertarAlimentos(ingredientesAlimentos)
        insertarAlimento(alimento)
        insertarDetalle(detalle)
        insertarIngredientes(relaciones)
    }

    /**
     * Obtiene el listado completo para el catálogo con detalle e ingredientes.
     */
    @Transaction
    @Query("SELECT * FROM Alimento WHERE origen = 'CUSTOM_RECIPE'")
    fun obtenerTodasLasRecetasCompletas(): Flow<List<RecetaCompleta>>

    /**
     * Obtiene una receta con todo su detalle e ingredientes por ID.
     */
    @Transaction
    @Query("SELECT * FROM Alimento WHERE id = :recetaId")
    fun obtenerRecetaPorId(recetaId: String): Flow<RecetaCompleta?>

    @Query("DELETE FROM Alimento WHERE id = :recetaId")
    suspend fun eliminarReceta(recetaId: String)
}