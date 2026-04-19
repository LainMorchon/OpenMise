package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Detalle_Receta")
data class DetalleRecetaEntity(
    // Esta es Primary Key y Foreign Key al mismo tiempo en el modelo lógico
    @PrimaryKey val alimento_id: String,
    val usuario_id: String,
    val descripcion: String,
    val pasos_preparacion: String?,
    val enlace_url: String?
)