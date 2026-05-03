package com.morchon.lain.data.repository

import com.morchon.lain.data.database.dao.RegistroDiarioDao
import com.morchon.lain.data.database.entity.aDominio
import com.morchon.lain.data.database.entity.aEntity
import com.morchon.lain.domain.model.RegistroDiario
import com.morchon.lain.domain.repository.RegistroDiarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class RegistroDiarioRepositoryImpl(
    private val dao: RegistroDiarioDao
) : RegistroDiarioRepository {
    override suspend fun guardarRegistro(registro: RegistroDiario) {
        dao.insertar(registro.aEntity())
    }

    override fun obtenerRegistrosPorFecha(usuarioId: String, fecha: LocalDate): Flow<List<RegistroDiario>> {
        return dao.obtenerRegistrosPorFecha(usuarioId, fecha.toString()).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    override suspend fun eliminarRegistro(id: Long) {
        dao.eliminarRegistro(id)
    }
}
