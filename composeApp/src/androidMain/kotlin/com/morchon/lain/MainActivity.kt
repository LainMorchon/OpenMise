package com.morchon.lain

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.morchon.lain.data.database.getDatabaseBuilder
import com.morchon.lain.di.initKoin
import org.koin.core.context.GlobalContext.getOrNull
import org.koin.dsl.module

import com.morchon.lain.ui.core.util.AndroidImageManager
import com.morchon.lain.ui.core.util.ImageManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Preparamos el módulo nativo de Android con la BD
        val androidModule = module {
            // Construimos la base de datos y la registramos en Koin
            single { getDatabaseBuilder(applicationContext).build() }
            
            // Registramos el gestor de imágenes para Android
            single<ImageManager> { AndroidImageManager(applicationContext) }
        }

        // 2. Arrancamos Koin (Solo si no se ha arrancado ya, para evitar crasheos al rotar la pantalla)
        if (getOrNull() == null) {
            initKoin(modulosExtra = listOf(androidModule))
        }

        // 3. Pintamos la UI multiplataforma
        setContent {
            App()
        }
    }
}