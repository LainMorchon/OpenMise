package com.morchon.lain.di

import androidx.lifecycle.SavedStateHandle
import com.morchon.lain.ui.login.LoginViewModel
import com.morchon.lain.ui.recetas.crear.CrearRecetaViewModel
import com.morchon.lain.ui.recetas.detalle.DetalleRecetaViewModel
import com.morchon.lain.ui.recetas.listado.ListadoRecetasViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // Usamos 'viewModel' para que Koin y Compose gestionen su ciclo de vida correctamente
    viewModel { LoginViewModel(get()) }
    viewModel { handle -> CrearRecetaViewModel(handle.get(), get()) }
    viewModel { ListadoRecetasViewModel(get()) }
    viewModel { handle -> DetalleRecetaViewModel(handle.get(), get()) }
}