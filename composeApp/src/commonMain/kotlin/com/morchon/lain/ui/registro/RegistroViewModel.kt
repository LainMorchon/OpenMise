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
        _estado.update { it.copy(email = nuevoEmail, error = null) }
    }

    fun registrarUsuario() {
        val nombre = _estado.value.nombre
        val email = _estado.value.email

        if (nombre.isBlank() || email.isBlank()) {
            _estado.update { it.copy(error = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true) }
            
            val resultado = registrarUsuarioUseCase(nombre, email)

            resultado.onSuccess {
                _estado.update { it.copy(estaCargando = false, registroExitoso = true) }
            }.onFailure { error ->
                _estado.update { it.copy(estaCargando = false, error = error.message) }
            }
        }
    }
}