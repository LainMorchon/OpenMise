package com.morchon.lain.data.database.model

import androidx.room.Embedded
import androidx.room.Relation
import com.morchon.lain.data.database.entity.AlimentoEntity
import com.morchon.lain.data.database.entity.DetalleRecetaEntity
import com.morchon.lain.data.database.entity.IngredienteRecetaEntity

/**
 * Une el detalle del ingrediente con la info nutricional del alimento que lo compone.
 */
data class IngredienteConInfo(
    @Embedded val detalle: IngredienteRecetaEntity,
    @Relation(
        parentColumn = "ingrediente_id",
        entityColumn = "id"
    )
    val alimentoInfo: AlimentoEntity
)

/**
 * Une Alimento + Detalle + Lista de Ingredientes.
 */
data class RecetaCompleta(
    @Embedded val alimento: AlimentoEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "alimento_id"
    )
    val detalle: DetalleRecetaEntity?,

    @Relation(
        entity = IngredienteRecetaEntity::class,
        parentColumn = "id",
        entityColumn = "receta_id"
    )
    val ingredientes: List<IngredienteConInfo>
)