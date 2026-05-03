package com.morchon.lain.ui.consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.usecase.consumo.BuscarConsumiblesUseCase
import com.morchon.lain.domain.usecase.registro.RegistrarConsumoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeleccionarAlimentoViewModel(
    private val buscarConsumiblesUseCase: BuscarConsumiblesUseCase,
    private val registrarConsumoUseCase: RegistrarConsumoUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(SeleccionarAlimentoState())
    val estado: StateFlow<SeleccionarAlimentoState> = _estado.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Carga inicial para mostrar recetas locales y alimentos sugeridos
        buscar("")
    }

    fun onQueryChange(nuevaQuery: String) {
        _estado.update { it.copy(query = nuevaQuery) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            buscar(nuevaQuery)
        }
    }

    private fun buscar(query: String) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val resultados = buscarConsumiblesUseCase(query)
            _estado.update { it.copy(listaResultados = resultados, cargando = false) }
        }
    }

    fun seleccionarAlimento(alimento: Alimento?) {
        _estado.update { it.copy(alimentoSeleccionado = alimento) }
    }

    fun onCantidadChange(cantidad: String) {
        _estado.update { it.copy(cantidadGramos = cantidad) }
    }

    fun onMomentoChange(momento: MomentoComida) {
        _estado.update { it.copy(momentoComida = momento) }
    }

    fun confirmarConsumo() {
        val alimento = _estado.value.alimentoSeleccionado ?: return
        val gramos = _estado.value.cantidadGramos.toDoubleOrNull() ?: 100.0

        viewModelScope.launch {
            registrarConsumoUseCase(
                alimento = alimento,
                cantidadGramos = gramos,
                momentoComida = _estado.value.momentoComida
            )
            _estado.update { it.copy(guardadoExitoso = true, alimentoSeleccionado = null) }
        }
    }

    fun resetExito() {
        _estado.update { it.copy(guardadoExitoso = false) }
    }
}
