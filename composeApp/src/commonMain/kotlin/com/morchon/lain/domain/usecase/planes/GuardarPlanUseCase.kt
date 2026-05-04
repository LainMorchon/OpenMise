package com.morchon.lain.domain.usecase.planes

import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.repository.PlanRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first

class GuardarPlanUseCase(
    private val planRepository: PlanRepository,
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(plan: Plan): Result<Long> {
        if (plan.nombre.isBlank()) {
            return Result.failure(Exception("El nombre del plan no puede estar vacío"))
        }
        if (plan.items.isEmpty()) {
            return Result.failure(Exception("El plan debe tener al menos un alimento"))
        }

        val usuario = usuarioRepository.obtenerUsuarioActivo().first()
            ?: return Result.failure(Exception("No hay un usuario activo"))

        val planConUsuario = plan.copy(usuarioId = usuario.id)
        val id = planRepository.guardarPlan(planConUsuario)
        return Result.success(id)
    }
}
