package com.morchon.lain.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository
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
            
            val nuevoUsuario = Usuario(
                id = "usr_${Random.nextInt(1000, 9999)}",
                nombre = nombre,
                email = email,
                estaLogeado = false
            )

            usuarioRepository.guardarUsuario(nuevoUsuario)
            _estado.update { it.copy(estaCargando = false, registroExitoso = true) }
        }
    }
}