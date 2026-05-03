package com.morchon.lain.ui.recetas.crear

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Ingrediente
import com.morchon.lain.domain.usecase.alimentos.BuscarAlimentosUseCase
import com.morchon.lain.domain.usecase.alimentos.GuardarAlimentoLocalUseCase
import com.morchon.lain.domain.usecase.recetas.GuardarRecetaUseCase
import com.morchon.lain.domain.usecase.recetas.ObtenerDetalleRecetaUseCase
import com.morchon.lain.domain.usecase.usuario.ObtenerUsuarioActivoUseCase
import com.morchon.lain.ui.core.util.ImageManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearRecetaViewModel(
    savedStateHandle: SavedStateHandle,
    private val obtenerDetalleRecetaUseCase: ObtenerDetalleRecetaUseCase,
    private val buscarAlimentosUseCase: BuscarAlimentosUseCase,
    private val guardarAlimentoLocalUseCase: GuardarAlimentoLocalUseCase,
    private val guardarRecetaUseCase: GuardarRecetaUseCase,
    private val obtenerUsuarioActivoUseCase: ObtenerUsuarioActivoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CrearRecetaState())
    val state: StateFlow<CrearRecetaState> = _state.asStateFlow()

    private var recetaId: String? = null
    private var busquedaJob: Job? = null

    init {
        // Observamos el cambio de ID en el SavedStateHandle
        viewModelScope.launch {
            savedStateHandle.getStateFlow<String?>("recetaId", null).collect { id ->
                val idLimpio = if (id == "null" || id.isNullOrBlank()) null else id
                recetaId = idLimpio
                
                if (idLimpio != null) {
                    cargarRecetaParaEditar(idLimpio)
                } else {
                    // RESET TOTAL si no hay ID
                    _state.value = CrearRecetaState()
                }
            }
        }
    }

    private fun cargarRecetaParaEditar(id: String) {
        viewModelScope.launch {
            obtenerDetalleRecetaUseCase(id).firstOrNull()?.let { r ->
                _state.update { 
                    it.copy(
                        nombre = r.nombre,
                        descripcion = r.descripcion,
                        pasosPreparacion = r.pasosPreparacion ?: "",
                        enlaceUrl = r.enlaceUrl ?: "",
                        imagenUrl = r.imagenUrl,
                        ingredientesAñadidos = r.ingredientes,
                        kcalTotales = r.kcalPor100g,
                        proteinasTotales = r.proteinasPor100g,
                        carbohidratosTotales = r.carbohidratosPor100g,
                        grasasTotales = r.grasasPor100g
                    )
                }
            }
        }
    }

    // --- FUNCIONES PARA ACTUALIZAR EL FORMULARIO ---

    fun onNombreCambiado(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre) }
    }

    fun onDescripcionCambiada(nuevaDesc: String) {
        _state.update { it.copy(descripcion = nuevaDesc) }
    }

    fun onPasosCambiados(nuevosPasos: String) {
        _state.update { it.copy(pasosPreparacion = nuevosPasos) }
    }

    fun onEnlaceCambiado(nuevoEnlace: String) {
        _state.update { it.copy(enlaceUrl = nuevoEnlace) }
    }

    fun onImagenSeleccionada(bytes: ByteArray?) {
        _state.update { it.copy(imagenByteArray = bytes, imagenUrl = if (bytes == null) null else it.imagenUrl) }
    }

    // --- BÚSQUEDA DE ALIMENTOS EN API ---

    fun onFiltroBusquedaCambiado(nuevoFiltro: String) {
        _state.update { it.copy(filtroBusqueda = nuevoFiltro) }
    }

    fun buscarAlimento(query: String, tipo: String? = null) {
        val tipoFinal = tipo ?: _state.value.filtroBusqueda
        busquedaJob?.cancel()

        if (query.isBlank()) {
            _state.update { it.copy(resultadosBusqueda = emptyList(), estaBuscando = false) }
            return
        }

        busquedaJob = viewModelScope.launch {
            delay(500)
            _state.update { it.copy(estaBuscando = true, filtroBusqueda = tipoFinal) }
            
            try {
                val resultados = buscarAlimentosUseCase(query, tipoFinal)
                _state.update { it.copy(resultadosBusqueda = resultados, estaBuscando = false) }
            } catch (e: Exception) {
                _state.update { it.copy(estaBuscando = false) }
            }
        }
    }

    // --- GESTIÓN DE INGREDIENTES Y MACROS ---

    fun anadirIngrediente(alimentoBase: Alimento, gramos: Float) {
        viewModelScope.launch {
            guardarAlimentoLocalUseCase(alimentoBase)

            val nuevoIngrediente = Ingrediente(alimento = alimentoBase, cantidadEnGramos = gramos)

            _state.update { estadoActual ->
                val nuevaLista = estadoActual.ingredientesAñadidos + nuevoIngrediente

                estadoActual.copy(
                    ingredientesAñadidos = nuevaLista,
                    kcalTotales = nuevaLista.sumOf { it.kcalTotales.toDouble() }.toFloat(),
                    proteinasTotales = nuevaLista.sumOf { it.proteinasTotales.toDouble() }.toFloat(),
                    carbohidratosTotales = nuevaLista.sumOf { it.carbohidratosTotales.toDouble() }.toFloat(),
                    grasasTotales = nuevaLista.sumOf { it.grasasTotales.toDouble() }.toFloat()
                )
            }
        }
    }

    fun eliminarIngrediente(ingrediente: Ingrediente) {
        _state.update { estadoActual ->
            val nuevaLista = estadoActual.ingredientesAñadidos - ingrediente

            estadoActual.copy(
                ingredientesAñadidos = nuevaLista,
                kcalTotales = nuevaLista.sumOf { it.kcalTotales.toDouble() }.toFloat(),
                proteinasTotales = nuevaLista.sumOf { it.proteinasTotales.toDouble() }.toFloat(),
                carbohidratosTotales = nuevaLista.sumOf { it.carbohidratosTotales.toDouble() }.toFloat(),
                grasasTotales = nuevaLista.sumOf { it.grasasTotales.toDouble() }.toFloat()
            )
        }
    }

    // --- GUARDADO FINAL ---

    fun guardarReceta() {
        val estadoActual = _state.value

        if (estadoActual.nombre.isBlank() || estadoActual.ingredientesAñadidos.isEmpty()) return

        _state.update { it.copy(estaGuardando = true) }

        viewModelScope.launch {
            val usuarioActual = obtenerUsuarioActivoUseCase().firstOrNull()
            val finalUsuarioId = usuarioActual?.id ?: "usuario_anonimo"

            val resultado = guardarRecetaUseCase(
                id = recetaId,
                nombre = estadoActual.nombre,
                descripcion = estadoActual.descripcion,
                pasosPreparacion = estadoActual.pasosPreparacion,
                enlaceUrl = estadoActual.enlaceUrl,
                usuarioId = finalUsuarioId,
                ingredientes = estadoActual.ingredientesAñadidos,
                imagenUrl = estadoActual.imagenUrl,
                imagenBytes = estadoActual.imagenByteArray
            )

            resultado.onSuccess {
                _state.update { it.copy(estaGuardando = false, guardadoExitoso = true) }
            }.onFailure {
                _state.update { it.copy(estaGuardando = false) }
            }
        }
    }
}
