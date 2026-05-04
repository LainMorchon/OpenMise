package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.morchon.lain.domain.model.Plan

@Entity(tableName = "Plan")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuario_id: String,
    val nombre: String,
    val descripcion: String?
)

fun PlanEntity.aDominio(): Plan {
    return Plan(
        id = id,
        usuarioId = usuario_id,
        nombre = nombre,
        descripcion = descripcion,
        items = emptyList() // Se cargan aparte
    )
}

fun Plan.aEntity(): PlanEntity {
    return PlanEntity(
        id = id,
        usuario_id = usuarioId,
        nombre = nombre,
        descripcion = descripcion
    )
}
