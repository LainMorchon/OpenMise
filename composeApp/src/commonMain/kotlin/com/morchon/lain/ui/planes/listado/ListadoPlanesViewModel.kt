package com.morchon.lain.ui.planes.listado

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.usecase.planes.AplicarPlanUseCase
import com.morchon.lain.domain.usecase.planes.EliminarPlanUseCase
import com.morchon.lain.domain.usecase.planes.ObtenerPlanesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ListadoPlanesViewModel(
    private val obtenerPlanesUseCase: ObtenerPlanesUseCase,
    private val eliminarPlanUseCase: EliminarPlanUseCase,
    private val aplicarPlanUseCase: AplicarPlanUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(ListadoPlanesState())
    val estado: StateFlow<ListadoPlanesState> = _estado.asStateFlow()

    init {
        cargarPlanes()
    }

    private fun cargarPlanes() {
        viewModelScope.launch {
            _estado.update { it.copy(estaCargando = true) }
            obtenerPlanesUseCase().collect { planes ->
                _estado.update { it.copy(planes = planes, estaCargando = false) }
            }
        }
    }

    fun alSolicitarEliminarPlan(plan: Plan) {
        _estado.update { it.copy(planAEliminar = plan, mostrarDialogoEliminar = true) }
    }

    fun cancelarEliminacion() {
        _estado.update { it.copy(planAEliminar = null, mostrarDialogoEliminar = false) }
    }

    fun confirmarEliminacion() {
        val plan = _estado.value.planAEliminar ?: return
        viewModelScope.launch {
            eliminarPlanUseCase(plan.id)
            _estado.update { it.copy(planAEliminar = null, mostrarDialogoEliminar = false) }
        }
    }

    fun aplicarPlan(plan: Plan) {
        viewModelScope.launch {
            val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            aplicarPlanUseCase(plan, hoy)
            // Aquí se podría añadir un mensaje de éxito en el estado
        }
    }
}
