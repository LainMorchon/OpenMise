package com.morchon.lain.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.morchon.lain.data.database.entity.AlimentoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para la gestión de la tabla de Alimentos.
 * Permite persistir tanto alimentos básicos como los obtenidos de la API.
 */
@Dao
interface AlimentoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarAlimento(alimento: AlimentoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarAlimentos(alimentos: List<AlimentoEntity>)

    @Query("SELECT * FROM Alimento WHERE id = :id")
    suspend fun obtenerAlimentoPorId(id: String): AlimentoEntity?

    @Query("SELECT * FROM Alimento")
    fun obtenerTodosLosAlimentos(): Flow<List<AlimentoEntity>>
}
