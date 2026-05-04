package com.morchon.lain.domain.usecase.planes

import com.morchon.lain.domain.repository.PlanRepository

class EliminarPlanUseCase(
    private val planRepository: PlanRepository
) {
    suspend operator fun invoke(planId: Long) {
        planRepository.eliminarPlan(planId)
    }
}
