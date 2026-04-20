package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato que define qué se puede hacer con los datos del usuario.
 * La capa de Dominio dicta las reglas, la capa de Datos (Data) decidirá cómo cumplirlas.
 */
interface UsuarioRepository {
    /**
     * Emite el usuario actual.
     */
    fun obtenerUsuarioActivo(): Flow<Usuario?>

    /**
     * Obtiene la lista de todos los usuarios registrados localmente.
     */
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    /**
     * Busca un usuario por email.
     */
    suspend fun obtenerUsuarioPorEmail(email: String): Usuario?

    /**
     * Guarda un usuario (Registro/Actualización).
     */
    suspend fun guardarUsuario(usuario: Usuario)

    /**
     * Marca a un usuario como el "activo" en la sesión.
     */
    suspend fun setUsuarioActivo(usuarioId: String)

    /**
     * Elimina los datos de sesión local.
     */
    suspend fun cerrarSesion()
}