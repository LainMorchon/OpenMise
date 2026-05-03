package com.morchon.lain.ui.recetas.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.usecase.recetas.ObtenerRecetasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListadoRecetasViewModel(
    private val obtenerRecetasUseCase: ObtenerRecetasUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ListadoRecetasState())
    val state: StateFlow<ListadoRecetasState> = _state.asStateFlow()

    init {
        cargarRecetas()
    }

    private fun cargarRecetas() {
        viewModelScope.launch {
            obtenerRecetasUseCase()
                .onStart { _state.update { it.copy(estaCargando = true) } }
                .catch { error ->
                    _state.update { it.copy(estaCargando = false, error = error.message) }
                }
                .collect { recetas ->
                    _state.update { 
                        it.copy(
                            recetas = recetas, 
                            estaCargando = false,
                            error = null
                        ) 
                    }
                }
        }
    }
}