package com.morchon.lain.domain.usecase.planes

import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.usecase.registro.RegistrarConsumoUseCase
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber

/**
 * Caso de uso para volcar todos los alimentos de un plan al registro diario
 * de una fecha específica.
 */
class AplicarPlanUseCase(
    private val registrarConsumoUseCase: RegistrarConsumoUseCase
) {
    suspend operator fun invoke(plan: Plan, fecha: LocalDate) {
        val itemsAAplicar = if (plan.tipo == "SEMANAL") {
            // Si es semanal, filtramos por el día de la semana de la fecha de destino (1-7)
            val diaDestino = fecha.dayOfWeek.isoDayNumber
            plan.items.filter { it.indiceDia == diaDestino }
        } else {
            // Si es de un solo día, aplicamos todo
            plan.items
        }

        itemsAAplicar.forEach { item ->
            registrarConsumoUseCase(
                alimento = item.alimento,
                cantidadGramos = item.cantidadGramos,
                momentoComida = item.momentoComida,
                fecha = fecha
            )
        }
    }
}
