package com.morchon.lain.di

import com.morchon.lain.ui.login.LoginViewModel
import com.morchon.lain.ui.registro.RegistroViewModel
import com.morchon.lain.ui.home.HomeViewModel
import com.morchon.lain.ui.recetas.crear.CrearRecetaViewModel
import com.morchon.lain.ui.recetas.detalle.DetalleRecetaViewModel
import com.morchon.lain.ui.recetas.listado.ListadoRecetasViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // Definición de ViewModels con sus dependencias (UseCases) actualizadas tras la refactorización
    
    // Login: requiere ObtenerUsuariosUseCase y LoginUseCase
    viewModel { LoginViewModel(get(), get()) }
    
    // Registro: requiere RegistrarUsuarioUseCase
    viewModel { RegistroViewModel(get()) }
    
    // Home: requiere ObtenerUsuarioActivoUseCase y CerrarSesionUseCase
    viewModel { HomeViewModel(get(), get()) }
    
    // Crear Receta: requiere SavedStateHandle + 5 UseCases obligatorios
    viewModel { handle -> 
        CrearRecetaViewModel(handle.get(), get(), get(), get(), get(), get()) 
    }
    
    // Listado Recetas: requiere ObtenerRecetasUseCase
    viewModel { ListadoRecetasViewModel(get()) }
    
    // Detalle Receta: requiere SavedStateHandle + 2 UseCases obligatorios
    viewModel { handle -> 
        DetalleRecetaViewModel(handle.get(), get(), get())
    }
}
