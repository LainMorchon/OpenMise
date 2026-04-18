package com.morchon.lain.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.morchon.lain.data.database.dao.UsuarioDao
import com.morchon.lain.data.database.entity.UsuarioEntity

@Database(
    [UsuarioEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
}