package com.morchon.lain.data.mapper

import com.morchon.lain.data.database.entity.ItemPlanEntity
import com.morchon.lain.data.database.entity.PlanEntity
import com.morchon.lain.data.database.model.ItemPlanConAlimento
import com.morchon.lain.data.database.model.PlanCompleto
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.Plan

// ====================================================================
// 1. DE ENTIDAD (ROOM) A DOMINIO (APP)
// ====================================================================

fun ItemPlanConAlimento.aDominio(): ItemPlan {
    return ItemPlan(
        id = item.id,
        alimento = alimento.aDominio(),
        cantidadGramos = item.cantidad_gramos,
        momentoComida = MomentoComida.valueOf(item.momento_comida),
        indiceDia = item.indice_dia
    )
}

fun PlanCompleto.aDominio(): Plan {
    return Plan(
        id = plan.id,
        usuarioId = plan.usuario_id,
        nombre = plan.nombre,
        tipo = plan.tipo,
        items = items.map { it.aDominio() }
    )
}

// ====================================================================
// 2. DE DOMINIO (APP) A ENTIDADES (ROOM)
// ====================================================================

fun Plan.aEntity(): PlanEntity {
    return PlanEntity(
        id = id,
        usuario_id = usuarioId,
        nombre = nombre,
        tipo = tipo
    )
}

fun ItemPlan.aEntity(planId: Long): ItemPlanEntity {
    return ItemPlanEntity(
        id = id,
        plan_id = planId,
        alimento_id = alimento.id,
        cantidad_gramos = cantidadGramos,
        momento_comida = momentoComida.name,
        indice_dia = indiceDia
    )
}
