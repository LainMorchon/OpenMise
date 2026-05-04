package com.morchon.lain.ui.consumo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.usecase.consumo.BuscarConsumiblesUseCase
import com.morchon.lain.domain.usecase.planes.AplicarPlanUseCase
import com.morchon.lain.domain.usecase.planes.ObtenerPlanesUseCase
import com.morchon.lain.domain.usecase.registro.RegistrarConsumoUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SeleccionarAlimentoViewModel(
    private val buscarConsumiblesUseCase: BuscarConsumiblesUseCase,
    private val registrarConsumoUseCase: RegistrarConsumoUseCase,
    private val obtenerPlanesUseCase: ObtenerPlanesUseCase,
    private val aplicarPlanUseCase: AplicarPlanUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(SeleccionarAlimentoState())
    val estado: StateFlow<SeleccionarAlimentoState> = _estado.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Carga inicial para mostrar recetas locales y alimentos sugeridos
        buscar("")
        cargarPlanes()
    }

    private fun cargarPlanes() {
        viewModelScope.launch {
            obtenerPlanesUseCase().collect { planes ->
                _estado.update { it.copy(planes = planes) }
            }
        }
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

    fun seleccionarPlan(plan: Plan?) {
        _estado.update { it.copy(planSeleccionado = plan) }
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

    fun registrarItemDePlan(item: ItemPlan) {
        viewModelScope.launch {
            registrarConsumoUseCase(
                alimento = item.alimento,
                cantidadGramos = item.cantidadGramos,
                momentoComida = item.momentoComida
            )
            // No cerramos pantalla para permitir añadir más cosas del plan si se quiere
        }
    }

    fun aplicarPlan(plan: Plan) {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            aplicarPlanUseCase(plan, hoy)
            _estado.update { it.copy(cargando = false, guardadoExitoso = true) }
        }
    }

    fun confirmarAplicarPlanCompleto() {
        val plan = _estado.value.planSeleccionado ?: return
        aplicarPlan(plan)
    }

    fun resetExito() {
        _estado.update { it.copy(guardadoExitoso = false) }
    }
}
