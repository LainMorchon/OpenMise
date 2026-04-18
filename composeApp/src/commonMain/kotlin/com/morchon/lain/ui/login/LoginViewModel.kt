package com.morchon.lain.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val usuarioRepository: UsuarioRepository
): ViewModel() {
    private val _estado = MutableStateFlow(LoginState())
    val estado: StateFlow<LoginState> = _estado.asStateFlow()

    fun alCambiarEmail(nuevoEmail: String) {
        _estado.update { it.copy(email = nuevoEmail, error = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _estado.update { it.copy(contrasena = nuevaContrasena, error = null) }
    }

    fun hacerLogin() {
        val emailActual = _estado.value.email
        val contrasenaActual = _estado.value.contrasena

        if(emailActual.isBlank() || contrasenaActual.isBlank()) {
            _estado.update { it.copy(error = "Rellena todos los campos, por favor") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true, error = null) }
            //Simulación de llamada a red de 2 segundos
            delay(2000)

            // Simulamos que el backend nos dice que OK y nos devuelve el perfil del usuario
            if (emailActual == "test@lain.com" && contrasenaActual == "123456") {
                val usuarioLogeado = Usuario(
                    id = "usr_1",
                    nombre = "Lain",
                    email = emailActual,
                    estaLogeado = true
                )

                //Guardado en Room usando el repositorio
                usuarioRepository.guardarUsuario(usuarioLogeado)
                _estado.update { it.copy(estaCargando = false, loginExitoso = true) }
            } else {
                _estado.update { it.copy(estaCargando = false, error = "Credenciales incorrectas") }

            }
        }
    }
}