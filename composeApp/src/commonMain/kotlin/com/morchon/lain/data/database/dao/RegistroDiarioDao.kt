package com.morchon.lain.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.morchon.lain.data.database.entity.RegistroDiarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDiarioDao {
    @Insert
    suspend fun insertar(registro: RegistroDiarioEntity)

    @Query("SELECT * FROM tabla_registro_diario WHERE usuario_id = :usuarioId AND fecha = :fecha")
    fun obtenerRegistrosPorFecha(usuarioId: String, fecha: String): Flow<List<RegistroDiarioEntity>>

    @Query("DELETE FROM tabla_registro_diario WHERE id = :id")
    suspend fun eliminarRegistro(id: Long)
}
