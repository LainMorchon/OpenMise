package com.morchon.lain.di

import com.morchon.lain.data.database.AppDatabase
import com.morchon.lain.data.repository.UsuarioRepositoryImpl
import com.morchon.lain.domain.repository.UsuarioRepository
import org.koin.dsl.module

val dataModule = module {
// 1. Proveemos el DAO (Koin buscará automáticamente la AppDatabase para sacarlo)
    single { get<AppDatabase>().usuarioDao() }

    // 2. Proveemos el Repositorio.
    // OJO: Lo registramos bajo su INTERFAZ (UsuarioRepository) para cumplir Clean Architecture.
    single<UsuarioRepository> { UsuarioRepositoryImpl(get()) }
}