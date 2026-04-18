package com.morchon.lain.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.morchon.lain.domain.model.Usuario

@Entity(tableName = "tabla_usuario")
data class UsuarioEntity (
    @PrimaryKey val id: String,
    val nombre: String,
    val email: String,
    val estaLogeado: Boolean
)

// Función de extensión (Mapper) para convertir de Entity (Data) a Modelo (Dominio)
fun UsuarioEntity.aDominio(): Usuario {
    return Usuario(
        id = id,
        nombre = nombre,
        email = email,
        estaLogeado = estaLogeado
    )
}

//Función inversa: de Dominio a Entity
fun Usuario.aEntity(): UsuarioEntity {
    return UsuarioEntity(
        id = id,
        nombre = nombre,
        email = email,
        estaLogeado = estaLogeado
    )
}