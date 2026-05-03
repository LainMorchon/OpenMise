package com.morchon.lain.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.ObjetivosNutricionales
import com.morchon.lain.domain.usecase.usuario.ActualizarObjetivosUseCase
import com.morchon.lain.domain.usecase.usuario.ObtenerUsuarioActivoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val obtenerUsuarioActivoUseCase: ObtenerUsuarioActivoUseCase,
    private val actualizarObjetivosUseCase: ActualizarObjetivosUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(PerfilState())
    val estado: StateFlow<PerfilState> = _estado.asStateFlow()

    init {
        cargarUsuario()
    }

    private fun cargarUsuario() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            obtenerUsuarioActivoUseCase().collect { usuario ->
                _estado.update {
                    it.copy(
                        usuario = usuario,
                        cargando = false,
                        kcal = usuario?.objetivos?.kcal?.toString() ?: "2000",
                        proteinas = usuario?.objetivos?.proteinas?.toString() ?: "150",
                        carbohidratos = usuario?.objetivos?.carbohidratos?.toString() ?: "200",
                        grasas = usuario?.objetivos?.grasas?.toString() ?: "65"
                    )
                }
            }
        }
    }

    fun onKcalChange(valor: String) { _estado.update { it.copy(kcal = valor) } }
    fun onProteinasChange(valor: String) { _estado.update { it.copy(proteinas = valor) } }
    fun onCarbohidratosChange(valor: String) { _estado.update { it.copy(carbohidratos = valor) } }
    fun onGrasasChange(valor: String) { _estado.update { it.copy(grasas = valor) } }

    fun guardarCambios() {
        viewModelScope.launch {
            val objetivos = ObjetivosNutricionales(
                kcal = _estado.value.kcal.toIntOrNull() ?: 2000,
                proteinas = _estado.value.proteinas.toIntOrNull() ?: 150,
                carbohidratos = _estado.value.carbohidratos.toIntOrNull() ?: 200,
                grasas = _estado.value.grasas.toIntOrNull() ?: 65
            )
            actualizarObjetivosUseCase(objetivos)
            _estado.update { it.copy(exito = true) }
        }
    }
    
    fun resetExito() {
        _estado.update { it.copy(exito = false) }
    }
}
