package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.morchon.lain.domain.model.ObjetivosNutricionales
import com.morchon.lain.domain.model.Usuario

@Entity(tableName = "tabla_usuario")
data class UsuarioEntity (
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val is_active: Boolean, // Flag para la sesión actual
    val kcal_objetivo: Int,
    val proteinas_objetivo: Int,
    val carbohidratos_objetivo: Int,
    val grasas_objetivo: Int
)

// Función de extensión (Mapper) para convertir de Entity (Data) a Modelo (Dominio)
fun UsuarioEntity.aDominio(): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        email = email,
        estaLogeado = is_active,
        objetivos = ObjetivosNutricionales(
            kcal = kcal_objetivo,
            proteinas = proteinas_objetivo,
            carbohidratos = carbohidratos_objetivo,
            grasas = grasas_objetivo
        )
    )
}

//Función inversa: de Dominio a Entity
fun Usuario.aEntity(): UsuarioEntity {
    return UsuarioEntity(
        id = id,
        nombre = nombre,
        email = email,
        is_active = estaLogeado,
        kcal_objetivo = objetivos.kcal,
        proteinas_objetivo = objetivos.proteinas,
        carbohidratos_objetivo = objetivos.carbohidratos,
        grasas_objetivo = objetivos.grasas
    )
}