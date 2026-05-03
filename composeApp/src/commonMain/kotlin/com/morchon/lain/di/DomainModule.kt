package com.morchon.lain.di

import com.morchon.lain.domain.usecase.alimentos.BuscarAlimentosUseCase
import com.morchon.lain.domain.usecase.alimentos.GuardarAlimentoLocalUseCase
import com.morchon.lain.domain.usecase.recetas.EliminarRecetaUseCase
import com.morchon.lain.domain.usecase.recetas.GuardarRecetaUseCase
import com.morchon.lain.domain.usecase.recetas.ObtenerDetalleRecetaUseCase
import com.morchon.lain.domain.usecase.recetas.ObtenerRecetasUseCase
import com.morchon.lain.domain.usecase.usuario.CerrarSesionUseCase
import com.morchon.lain.domain.usecase.usuario.LoginUseCase
import com.morchon.lain.domain.usecase.usuario.ObtenerUsuarioActivoUseCase
import com.morchon.lain.domain.usecase.usuario.ObtenerUsuariosUseCase
import com.morchon.lain.domain.usecase.usuario.RegistrarUsuarioUseCase
import org.koin.dsl.module

val domainModule = module {
    // --- RECETAS ---
    factory { ObtenerRecetasUseCase(get()) }
    factory { ObtenerDetalleRecetaUseCase(get()) }
    factory { EliminarRecetaUseCase(get()) }
    factory { GuardarRecetaUseCase(get(), getOrNull()) }

    // --- USUARIO ---
    factory { ObtenerUsuariosUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RegistrarUsuarioUseCase(get()) }
    factory { ObtenerUsuarioActivoUseCase(get()) }
    factory { CerrarSesionUseCase(get()) }

    // --- ALIMENTOS ---
    factory { BuscarAlimentosUseCase(get()) }
    factory { GuardarAlimentoLocalUseCase(get()) }
}
