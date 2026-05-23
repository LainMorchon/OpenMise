package com.morchon.lain.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.usecase.registro.ObtenerProgresoDiarioUseCase
import com.morchon.lain.domain.usecase.usuario.CerrarSesionUseCase
import com.morchon.lain.domain.usecase.usuario.EliminarUsuarioUseCase
import com.morchon.lain.domain.usecase.usuario.ObtenerUsuarioActivoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val obtenerUsuarioActivoUseCase: ObtenerUsuarioActivoUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val obtenerProgresoDiarioUseCase: ObtenerProgresoDiarioUseCase,
    private val eliminarUsuarioUseCase: EliminarUsuarioUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeState())
    val estado: StateFlow<HomeState> = _estado.asStateFlow()

    init {
        observarDatos()
    }

    private fun observarDatos() {
        viewModelScope.launch {
            obtenerUsuarioActivoUseCase().collect { usuario ->
                _estado.update { it.copy(usuario = usuario) }
            }
        }
        viewModelScope.launch {
            obtenerProgresoDiarioUseCase().collect { progreso ->
                _estado.update { it.copy(progreso = progreso) }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            cerrarSesionUseCase()
            _estado.update { it.copy(sesionCerrada = true) }
        }
    }

    fun eliminarCuenta() {
        val usuarioActual = estado.value.usuario ?: return
        val contrasenaIngresada = estado.value.contrasenaConfirmacion

        viewModelScope.launch {
            val resultado = eliminarUsuarioUseCase(usuarioActual.id, contrasenaIngresada)
            
            resultado.onSuccess {
                _estado.update { it.copy(sesionCerrada = true, mostrarDialogoEliminar = false) }
            }.onFailure { error ->
                _estado.update { it.copy(errorEliminar = error.message ?: "Error al eliminar la cuenta") }
            }
        }
    }

    fun mostrarDialogoEliminar(mostrar: Boolean) {
        _estado.update { 
            it.copy(
                mostrarDialogoEliminar = mostrar, 
                contrasenaConfirmacion = "", 
                errorEliminar = null 
            ) 
        }
    }

    fun alCambiarContrasenaConfirmacion(nuevaContrasena: String) {
        _estado.update { it.copy(contrasenaConfirmacion = nuevaContrasena, errorEliminar = null) }
    }
}