package com.morchon.lain.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(HomeState())
    val estado: StateFlow<HomeState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.obtenerUsuarioActivo().collect { usuario ->
                _estado.update { it.copy(usuario = usuario) }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            usuarioRepository.cerrarSesion()
            _estado.update { it.copy(sesionCerrada = true) }
        }
    }
}