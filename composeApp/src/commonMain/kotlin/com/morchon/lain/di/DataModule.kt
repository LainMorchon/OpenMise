package com.morchon.lain.di

import com.morchon.lain.data.database.AppDatabase
import com.morchon.lain.data.repository.UsuarioRepositoryImpl
import com.morchon.lain.domain.repository.UsuarioRepository
import org.koin.dsl.module
import com.morchon.lain.data.repository.RecetaRepositoryImpl
import com.morchon.lain.domain.repository.RecetaRepository

val dataModule = module {
    // --- USUARIO ---
    // 1. Proveemos el DAO de Usuario
    single { get<AppDatabase>().usuarioDao() }
    // 2. Proveemos el Repositorio de Usuario
    single<UsuarioRepository> { UsuarioRepositoryImpl(get()) }

    // --- RECETAS ---
    // 3. Proveemos el DAO de Recetas
    single { get<AppDatabase>().recetaDao() }
    // 4. Proveemos el Repositorio de Recetas
    single<RecetaRepository> { RecetaRepositoryImpl(get()) }
}