package com.morchon.lain.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.usecase.usuario.RegistrarUsuarioUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class RegistroViewModel(
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroState())
    val estado: StateFlow<RegistroState> = _estado.asStateFlow()

    fun alCambiarNombre(nuevoNombre: String) {
        _estado.update { it.copy(nombre = nuevoNombre, error = null) }
    }

    fun alCambiarEmail(nuevoEmail: String) {
        _estado.update { it.copy(email = nuevoEmail, errorEmail = null, error = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _estado.update { it.copy(contrasena = nuevaContrasena, errorContrasena = null, error = null) }
    }

    fun registrarUsuario() {
        val nombre = _estado.value.nombre
        val email = _estado.value.email
        val contrasena = _estado.value.contrasena

        var hayError = false

        if (nombre.isBlank() || email.isBlank() || contrasena.isBlank()) {
            _estado.update { it.copy(error = "Rellena todos los campos") }
            hayError = true
        }

        if (!esEmailValido(email)) {
            _estado.update { it.copy(errorEmail = "Formato de email inválido") }
            hayError = true
        }

        if (contrasena.length < 6) {
            _estado.update { it.copy(errorContrasena = "Mínimo 6 caracteres") }
            hayError = true
        }

        if (hayError) return

        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true) }
            
            val resultado = registrarUsuarioUseCase(nombre, email, contrasena)

            resultado.onSuccess {
                _estado.update { it.copy(estaCargando = false, registroExitoso = true) }
            }.onFailure { error ->
                _estado.update { it.copy(estaCargando = false, error = error.message) }
            }
        }
    }

    private fun esEmailValido(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return email.matches(emailRegex)
    }
}