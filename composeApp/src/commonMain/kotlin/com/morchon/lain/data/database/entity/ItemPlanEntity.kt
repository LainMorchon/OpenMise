package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Item_Plan",
    foreignKeys = [
        ForeignKey(
            entity = PlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["alimento_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("plan_id"), Index("alimento_id")]
)
data class ItemPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plan_id: Long,
    val alimento_id: String,
    val cantidad_gramos: Double,
    val momento_comida: String,
    val indice_dia: Int
)
