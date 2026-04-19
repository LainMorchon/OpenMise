package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ingrediente_Receta")
data class IngredienteRecetaEntity(
    @PrimaryKey val id: String,
    val receta_id: String,       // Apunta al Alimento Padre
    val ingrediente_id: String,  // Apunta al Alimento Hijo
    val cantidad_en_gramos: Float
)