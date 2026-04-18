package com.morchon.lain.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

/**
 * Función global para arrancar Koin en cualquier plataforma.
 * @param modulosExtra Permite inyectar dependencias específicas de cada plataforma (como el driver de Room para Android/iOS).
 */
fun initKoin(modulosExtra: List<Module> = emptyList()) {
    startKoin {
        modules(
            dataModule,
            uiModule,
            *modulosExtra.toTypedArray()
        )
    }
}