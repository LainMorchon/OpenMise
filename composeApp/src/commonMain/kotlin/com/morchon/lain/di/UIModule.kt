package com.morchon.lain.di

import com.morchon.lain.ui.login.LoginViewModel
import com.morchon.lain.ui.registro.RegistroViewModel
import com.morchon.lain.ui.home.HomeViewModel
import com.morchon.lain.ui.consumo.SeleccionarAlimentoViewModel
import com.morchon.lain.ui.perfil.PerfilViewModel
import com.morchon.lain.ui.recetas.crear.CrearRecetaViewModel
import com.morchon.lain.ui.recetas.detalle.DetalleRecetaViewModel
import com.morchon.lain.ui.recetas.listado.ListadoRecetasViewModel
import com.morchon.lain.ui.planes.listado.ListadoPlanesViewModel
import com.morchon.lain.ui.planes.editar.EditarPlanViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // Definición de ViewModels con sus dependencias (UseCases)
    
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistroViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { SeleccionarAlimentoViewModel(get(), get(), get(), get()) }
    viewModel { PerfilViewModel(get(), get()) }
    
    viewModel { handle -> 
        CrearRecetaViewModel(handle.get(), get(), get(), get(), get(), get()) 
    }
    
    viewModel { ListadoRecetasViewModel(get()) }
    
    viewModel { handle -> 
        DetalleRecetaViewModel(handle.get(), get(), get())
    }

    // --- PLANES ---
    viewModel { ListadoPlanesViewModel(get(), get(), get()) }
    viewModel { handle -> 
        EditarPlanViewModel(handle.get(), get(), get(), get())
    }
}
