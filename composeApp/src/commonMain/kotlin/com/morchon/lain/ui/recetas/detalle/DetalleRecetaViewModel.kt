package com.morchon.lain.ui.recetas.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.usecase.recetas.EliminarRecetaUseCase
import com.morchon.lain.domain.usecase.recetas.ObtenerDetalleRecetaUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleRecetaViewModel(
    savedStateHandle: SavedStateHandle,
    private val obtenerDetalleRecetaUseCase: ObtenerDetalleRecetaUseCase,
    private val eliminarRecetaUseCase: EliminarRecetaUseCase
) : ViewModel() {

    private val recetaId: String = checkNotNull(savedStateHandle["recetaId"])

    private val _state = MutableStateFlow(DetalleRecetaState())
    val state: StateFlow<DetalleRecetaState> = _state.asStateFlow()

    init {
        cargarReceta()
    }

    private fun cargarReceta() {
        viewModelScope.launch {
            obtenerDetalleRecetaUseCase(recetaId)
                .onStart { _state.update { it.copy(estaCargando = true) } }
                .catch { error ->
                    _state.update { it.copy(estaCargando = false, error = error.message) }
                }
                .collect { receta ->
                    _state.update { 
                        it.copy(
                            receta = receta, 
                            estaCargando = false,
                            error = if (receta == null) "Receta no encontrada" else null
                        ) 
                    }
                }
        }
    }

    fun onBorrarClick() {
        _state.update { it.copy(mostrarConfirmacionBorrado = true) }
    }

    fun onConfirmarBorrado() {
        _state.update { it.copy(mostrarConfirmacionBorrado = false, estaCargando = true) }
        viewModelScope.launch {
            try {
                eliminarRecetaUseCase(recetaId)
                _state.update { it.copy(estaCargando = false, borradoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(estaCargando = false, error = e.message) }
            }
        }
    }

    fun onCancelarBorrado() {
        _state.update { it.copy(mostrarConfirmacionBorrado = false) }
    }
}