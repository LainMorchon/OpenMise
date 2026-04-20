package com.morchon.lain.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.morchon.lain.data.database.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    /**
     * Devuelve el usuario que tiene el flag de 'is_active' a true.
     */
    @Query("SELECT * FROM tabla_usuario WHERE is_active = 1 LIMIT 1")
    fun obtenerUsuarioActivo(): Flow<UsuarioEntity?>

    /**
     * Devuelve todos los usuarios registrados.
     */
    @Query("SELECT * FROM tabla_usuario")
    fun obtenerTodosLosUsuarios(): Flow<List<UsuarioEntity>>

    /**
     * Busca por email (para login).
     */
    @Query("SELECT * FROM tabla_usuario WHERE email = :email LIMIT 1")
    suspend fun obtenerUsuarioPorEmail(email: String): UsuarioEntity?

    /**
     * Busca por ID.
     */
    @Query("SELECT * FROM tabla_usuario WHERE id = :id LIMIT 1")
    suspend fun obtenerUsuarioPorId(id: String): UsuarioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuario(usuario: UsuarioEntity)

    /**
     * Actualiza el estado de activo de un usuario.
     */
    @Query("UPDATE tabla_usuario SET is_active = :active WHERE id = :id")
    suspend fun setEstadoActivo(id: String, active: Boolean)

    /**
     * Pone a todos los usuarios como inactivos.
     */
    @Query("UPDATE tabla_usuario SET is_active = 0")
    suspend fun desactivarTodos()

    @Query("DELETE FROM tabla_usuario")
    suspend fun eliminarSesion()
}