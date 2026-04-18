package com.morchon.lain.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Función nativa de Android para construir la base de datos de Room.
 * Usa el Context para encontrar la ruta de archivos de la aplicación.
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("lain_nutricion.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}