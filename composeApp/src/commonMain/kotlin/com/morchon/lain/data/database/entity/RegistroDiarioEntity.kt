package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.RegistroDiario
import kotlinx.datetime.LocalDate

@Entity(tableName = "tabla_registro_diario")
data class RegistroDiarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuario_id: String,
    val alimento_id: String,
    val nombre_alimento: String,
    val fecha: String, // Almacenado como ISO String
    val cantidad_gramos: Double,
    val momento_comida: String,
    // Snapshot de macros
    val kcal_snapshot: Double,
    val proteinas_snapshot: Double,
    val carbohidratos_snapshot: Double,
    val grasas_snapshot: Double
)

fun RegistroDiarioEntity.aDominio(): RegistroDiario {
    return RegistroDiario(
        id = id,
        usuarioId = usuario_id,
        alimentoId = alimento_id,
        nombreAlimento = nombre_alimento,
        fecha = LocalDate.parse(fecha),
        cantidadGramos = cantidad_gramos,
        momentoComida = MomentoComida.valueOf(momento_comida),
        kcalHistoricas = kcal_snapshot,
        proteinasHistoricas = proteinas_snapshot,
        carbohidratosHistoricos = carbohidratos_snapshot,
        grasasHistoricas = grasas_snapshot
    )
}

fun RegistroDiario.aEntity(): RegistroDiarioEntity {
    return RegistroDiarioEntity(
        id = id,
        usuario_id = usuarioId,
        alimento_id = alimentoId,
        nombre_alimento = nombreAlimento,
        fecha = fecha.toString(),
        cantidad_gramos = cantidadGramos,
        momento_comida = momentoComida.name,
        kcal_snapshot = kcalHistoricas,
        proteinas_snapshot = proteinasHistoricas,
        carbohidratos_snapshot = carbohidratosHistoricos,
        grasas_snapshot = grasasHistoricas
    )
}
