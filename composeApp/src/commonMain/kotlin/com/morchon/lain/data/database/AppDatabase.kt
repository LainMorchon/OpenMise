package com.morchon.lain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.morchon.lain.data.database.dao.RecetaDao
import com.morchon.lain.data.database.dao.UsuarioDao
import com.morchon.lain.data.database.entity.DetalleRecetaEntity
import com.morchon.lain.data.database.entity.IngredienteRecetaEntity
import com.morchon.lain.data.database.entity.UsuarioEntity
import com.morchon.lain.data.database.entity.AlimentoEntity

@Database(
    entities = [
        UsuarioEntity::class,
        AlimentoEntity::class,
        DetalleRecetaEntity::class,
        IngredienteRecetaEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun recetaDao(): RecetaDao
}