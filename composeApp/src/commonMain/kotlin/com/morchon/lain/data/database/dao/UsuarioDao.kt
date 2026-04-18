package com.morchon.lain.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.morchon.lain.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Usamos Flow para que la UI se entere al instante si el usuario cambia
    @Query("SELECT * FROM tabla_usuario LIMIT 1")
    fun obtenerUsuarioActivo(): Flow<UsuarioEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuario(usuario: UsuarioEntity)

    @Query("DELETE FROM tabla_usuario")
    suspend fun eliminarSesion()
}