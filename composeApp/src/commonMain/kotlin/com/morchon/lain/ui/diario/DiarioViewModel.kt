package com.morchon.lain.ui.diario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.usecase.registro.EliminarRegistroUseCase
import com.morchon.lain.domain.usecase.registro.ObtenerRegistrosDiariosUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class DiarioViewModel(
    private val obtenerRegistrosUseCase: ObtenerRegistrosDiariosUseCase,
    private val eliminarRegistroUseCase: EliminarRegistroUseCase
) : ViewModel() {

    private val _fechaSeleccionada = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val estado: StateFlow<DiarioState> = _fechaSeleccionada
        .flatMapLatest { fecha ->
            obtenerRegistrosUseCase(fecha).map { registros ->
                DiarioState(
                    fechaSeleccionada = fecha,
                    registros = registros,
                    totalKcal = registros.sumOf { it.kcalHistoricas },
                    totalProteinas = registros.sumOf { it.proteinasHistoricas },
                    totalCarbohidratos = registros.sumOf { it.carbohidratosHistoricos },
                    totalGrasas = registros.sumOf { it.grasasHistoricas },
                    estaCargando = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DiarioState(fechaSeleccionada = _fechaSeleccionada.value, estaCargando = true)
        )

    fun alCambiarFecha(dias: Int) {
        val nuevaFecha = _fechaSeleccionada.value.plus(DatePeriod(days = dias))
        _fechaSeleccionada.value = nuevaFecha
    }

    fun eliminarRegistro(registroId: Long) {
        viewModelScope.launch {
            eliminarRegistroUseCase(registroId)
        }
    }
}
