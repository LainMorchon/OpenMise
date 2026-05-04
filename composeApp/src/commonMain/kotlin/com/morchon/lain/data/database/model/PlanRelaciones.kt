package com.morchon.lain.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.morchon.lain.data.database.entity.AlimentoEntity
import com.morchon.lain.data.database.entity.ItemPlanEntity
import com.morchon.lain.data.database.entity.PlanEntity

/**
 * Une un item del plan con la info del alimento base.
 */
data class ItemPlanConAlimento(
    @Embedded val item: ItemPlanEntity,
    @Relation(
        parentColumn = "alimento_id",
        entityColumn = "id"
    )
    val alimento: AlimentoEntity
)

/**
 * Une el Plan con todos sus items cargados.
 */
data class PlanCompleto(
    @Embedded val plan: PlanEntity,
    @Relation(
        entity = ItemPlanEntity::class,
        parentColumn = "id",
        entityColumn = "plan_id"
    )
    val items: List<ItemPlanConAlimento>
)
