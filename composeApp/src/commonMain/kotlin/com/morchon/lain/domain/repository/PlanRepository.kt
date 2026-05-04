package com.morchon.lain.domain.repository

import com.morchon.lain.domain.model.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    fun obtenerPlanesPorUsuario(usuarioId: String): Flow<List<Plan>>
    suspend fun obtenerPlanPorId(id: Long): Plan?
    suspend fun guardarPlan(plan: Plan): Long
    suspend fun eliminarPlan(planId: Long)
}
