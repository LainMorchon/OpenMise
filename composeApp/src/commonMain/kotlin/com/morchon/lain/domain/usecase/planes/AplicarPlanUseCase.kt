package com.morchon.lain.domain.usecase.planes

import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.usecase.registro.RegistrarConsumoUseCase
import kotlinx.datetime.LocalDate

/**
 * Caso de uso para volcar todos los alimentos de un plan al registro diario
 * de una fecha específica.
 */
class AplicarPlanUseCase(
    private val registrarConsumoUseCase: RegistrarConsumoUseCase
) {
    suspend operator fun invoke(plan: Plan, fecha: LocalDate) {
        plan.items.forEach { item ->
            registrarConsumoUseCase(
                alimento = item.alimento,
                cantidadGramos = item.cantidadGramos,
                momentoComida = item.momentoComida,
                fecha = fecha
            )
        }
    }
}
