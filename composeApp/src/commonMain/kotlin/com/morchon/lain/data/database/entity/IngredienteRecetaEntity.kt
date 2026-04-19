package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Ingrediente_Receta",
    foreignKeys = [
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["receta_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["ingrediente_id"],
            onDelete = ForeignKey.RESTRICT // No permite borrar un ingrediente si está en una receta
        )
    ],
    indices = [Index("receta_id"), Index("ingrediente_id")]
)
data class IngredienteRecetaEntity(
    @PrimaryKey val id: String,
    val receta_id: String,
    val ingrediente_id: String,
    val cantidad_en_gramos: Float
)