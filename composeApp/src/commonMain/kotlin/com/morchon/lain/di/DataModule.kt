package com.morchon.lain.di

import com.morchon.lain.data.database.AppDatabase
import com.morchon.lain.data.repository.UsuarioRepositoryImpl
import com.morchon.lain.domain.repository.UsuarioRepository
import org.koin.dsl.module
import com.morchon.lain.data.repository.RecetaRepositoryImpl
import com.morchon.lain.domain.repository.RecetaRepository
import com.morchon.lain.data.remote.FatSecretApiService
import com.morchon.lain.domain.repository.AlimentoRepository
import com.morchon.lain.data.repository.AlimentoRepositoryImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val dataModule = module {
    // --- RED (Ktor) ---
    // Configuramos el cliente HTTP global con soporte para JSON
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true // Importante: Ignora campos de la API que no usemos
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    // Proveemos el servicio de la API de FatSecret
    single { FatSecretApiService(get()) }

    // Repositorio de Alimentos (Búsqueda API)
    single<AlimentoRepository> { AlimentoRepositoryImpl(get()) }

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