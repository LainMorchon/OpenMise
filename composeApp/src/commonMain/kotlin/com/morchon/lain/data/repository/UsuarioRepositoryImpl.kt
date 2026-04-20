package com.morchon.lain.data.repository

import com.morchon.lain.data.database.dao.UsuarioDao
import com.morchon.lain.data.database.entity.aDominio
import com.morchon.lain.data.database.entity.aEntity
import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


/**
 * Implementación real del repositorio.
 * Conoce a Room (UsuarioDao), pero le devuelve a la app datos puros (Usuario).
 */
class UsuarioRepositoryImpl(
    private val usuarioDao: UsuarioDao
) : UsuarioRepository {
    override fun obtenerUsuarioActivo(): Flow<Usuario?> {
        return usuarioDao.obtenerUsuarioActivo().map { it?.aDominio() }
    }

    override fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios().map { lista ->
            lista.map { it.aDominio() }
        }
    }

    override suspend fun obtenerUsuarioPorEmail(email: String): Usuario? {
        return usuarioDao.obtenerUsuarioPorEmail(email)?.aDominio()
    }

    override suspend fun guardarUsuario(usuario: Usuario) {
        usuarioDao.guardarUsuario(usuario.aEntity())
    }

    override suspend fun setUsuarioActivo(usuarioId: String) {
        usuarioDao.desactivarTodos()
        usuarioDao.setEstadoActivo(usuarioId, true)
    }

    override suspend fun cerrarSesion() {
        usuarioDao.desactivarTodos()
    }
}