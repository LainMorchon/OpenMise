package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato que define qué se puede hacer con los datos del usuario.
 * La capa de Dominio dicta las reglas, la capa de Datos (Data) decidirá cómo cumplirlas.
 */
interface UsuarioRepository {
    /**
     * Emite el usuario actual. Usamos Flow para que la UI reaccione
     * automáticamente si el usuario cierra sesión o se actualiza.
     */
    fun obtenerUsuarioActivo(): Flow<Usuario?>

    /**
     * Guarda la sesión del usuario (lo implementaremos con Room).
     */
    suspend fun guardarUsuario(usuario: Usuario)

    /**
     * Elimina los datos de sesión local.
     */
    suspend fun cerrarSesion()
}