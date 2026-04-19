package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Alimento")
data class AlimentoEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val origen: String, // "API_RAW", "API_COMMERCIAL", "CUSTOM_RECIPE"
    val kcal_por_100g: Float,
    val proteinas_por_100g: Float,
    val carbohidratos_por_100g: Float,
    val grasas_por_100g: Float
)