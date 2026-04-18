package com.morchon.lain

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.morchon.lain.ui.login.LoginScreen
import com.morchon.lain.ui.login.LoginViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        // Envolvemos la app en el contexto de Koin
        KoinContext {
            // Le pedimos a Koin que nos consiga la instancia del ViewModel
            val loginViewModel = koinViewModel<LoginViewModel>()

            // Mostramos la pantalla
            LoginScreen(
                viewModel = loginViewModel,
                alNavegarAlHome = {
                    // Aquí configuraremos el NavHost en el futuro
                    println("¡Login correcto! Navegando al Home...")
                }
            )
        }
    }
}