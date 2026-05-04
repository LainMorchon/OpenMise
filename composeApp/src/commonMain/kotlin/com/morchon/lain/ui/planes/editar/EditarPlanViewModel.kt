package com.morchon.lain.ui.planes.editar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.ItemPlan
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.repository.PlanRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import com.morchon.lain.domain.usecase.planes.GuardarPlanUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditarPlanViewModel(
    savedStateHandle: SavedStateHandle,
    private val planRepository: PlanRepository,
    private val guardarPlanUseCase: GuardarPlanUseCase,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val planIdStr: String? = savedStateHandle["planId"]
    private val planId: Long? = if (planIdStr == null || planIdStr == "null") null else planIdStr.toLongOrNull()

    private val _state = MutableStateFlow(EditarPlanState())
    val state: StateFlow<EditarPlanState> = _state.asStateFlow()

    init {
        cargarPlan()
    }

    private fun cargarPlan() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            val usuario = usuarioRepository.obtenerUsuarioActivo().firstOrNull()
            val usuarioId = usuario?.id ?: ""

            if (planId != null && planId != -1L) {
                val planExistente = planRepository.obtenerPlanPorId(planId)
                if (planExistente != null) {
                    _state.update { it.copy(plan = planExistente, isLoading = false) }
                } else {
                    _state.update { it.copy(
                        plan = Plan(usuarioId = usuarioId, nombre = "", tipo = "DIA_UNICO"),
                        isLoading = false
                    ) }
                }
            } else {
                _state.update { it.copy(
                    plan = Plan(usuarioId = usuarioId, nombre = "", tipo = "DIA_UNICO"),
                    isLoading = false
                ) }
            }
        }
    }

    fun onNombreChanged(nuevoNombre: String) {
        _state.update { it.copy(plan = it.plan.copy(nombre = nuevoNombre)) }
    }

    fun onTipoChanged(nuevoTipo: String) {
        _state.update { it.copy(plan = it.plan.copy(tipo = nuevoTipo)) }
    }

    fun agregarAlimento(alimento: Alimento, cantidad: Double, momento: MomentoComida) {
        val nuevoItem = ItemPlan(
            alimento = alimento,
            cantidadGramos = cantidad,
            momentoComida = momento,
            indiceDia = 0 // Por ahora simplificado a día único
        )
        _state.update { 
            it.copy(
                plan = it.plan.copy(items = it.plan.items + nuevoItem),
                showBuscadorAlimentos = false
            ) 
        }
    }

    fun eliminarItem(item: ItemPlan) {
        _state.update { 
            it.copy(plan = it.plan.copy(items = it.plan.items - item))
        }
    }

    fun toggleBuscador(show: Boolean) {
        _state.update { it.copy(showBuscadorAlimentos = show) }
    }

    fun guardarPlan() {
        viewModelScope.launch {
            if (_state.value.plan.nombre.isBlank()) {
                _state.update { it.copy(error = "El nombre no puede estar vacío") }
                return@launch
            }

            try {
                _state.update { it.copy(isLoading = true) }
                guardarPlanUseCase(_state.value.plan)
                _state.update { it.copy(isLoading = false, guardadoExitoso = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
