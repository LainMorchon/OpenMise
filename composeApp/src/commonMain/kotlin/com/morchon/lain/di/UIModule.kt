package com.morchon.lain.di

import com.morchon.lain.ui.login.LoginViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    // Usamos 'viewModel' para que Koin y Compose gestionen su ciclo de vida correctamente
    viewModel { LoginViewModel(get()) }
}