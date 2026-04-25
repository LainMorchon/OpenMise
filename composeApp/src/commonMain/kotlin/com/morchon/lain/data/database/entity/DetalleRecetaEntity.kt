package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Detalle_Receta",
    foreignKeys = [
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["alimento_id"],
            onDelete = ForeignKey.CASCADE // Limpieza automática
        )
    ],
    indices = [Index("alimento_id")] // Recomendado por Room para FKs
)
data class DetalleRecetaEntity(
    @PrimaryKey val alimento_id: String,
    val usuario_id: String,
    val descripcion: String,
    val pasos_preparacion: String?,
    val enlace_url: String?,
    val imagen_url: String?
)