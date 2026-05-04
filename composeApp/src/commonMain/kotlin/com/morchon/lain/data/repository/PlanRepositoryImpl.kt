package com.morchon.lain.data.repository

import com.morchon.lain.data.database.dao.PlanDao
import com.morchon.lain.data.mapper.aDominio
import com.morchon.lain.data.mapper.aEntity
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlanRepositoryImpl(
    private val planDao: PlanDao
) : PlanRepository {

    override fun obtenerPlanesPorUsuario(usuarioId: String): Flow<List<Plan>> {
        return planDao.obtenerPlanesCompletosPorUsuario(usuarioId).map { lista ->
            lista.map { it.aDominio() }
        }
    }

    override suspend fun obtenerPlanPorId(id: Long): Plan? {
        return planDao.obtenerPlanCompletoPorId(id)?.aDominio()
    }

    override suspend fun guardarPlan(plan: Plan): Long {
        val entity = plan.aEntity()
        val items = plan.items.map { it.aEntity(planId = plan.id) }
        return planDao.guardarPlanCompleto(entity, items)
    }

    override suspend fun eliminarPlan(planId: Long) {
        planDao.eliminarPlan(planId)
    }
}
