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

    init {
        viewModelScope.launch {
            usuarioRepository.obtenerTodosLosUsuarios().collect { usuarios ->
                _estado.update { it.copy(usuariosRegistrados = usuarios) }
            }
        }
    }

    fun alCambiarEmail(nuevoEmail: String) {
        _estado.update { it.copy(email = nuevoEmail, error = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _estado.update { it.copy(contrasena = nuevaContrasena, error = null) }
    }

    fun seleccionarUsuario(usuario: Usuario) {
        _estado.update { it.copy(email = usuario.email, error = null) }
    }

    fun hacerLogin() {
        val emailActual = _estado.value.email
        // Nota: De momento no validamos contraseña contra BD real, 
        // pero buscamos si el usuario existe por email.
        
        if(emailActual.isBlank()) {
            _estado.update { it.copy(error = "Introduce un email") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true, error = null) }
            
            val usuario = usuarioRepository.obtenerUsuarioPorEmail(emailActual)

            if (usuario != null) {
                usuarioRepository.setUsuarioActivo(usuario.id)
                _estado.update { it.copy(estaCargando = false, loginExitoso = true) }
            } else {
                _estado.update { it.copy(estaCargando = false, error = "Usuario no encontrado") }
            }
        }
    }
}