package com.morchon.lain.ui.recetas.crear

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Ingrediente
import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.AlimentoRepository
import com.morchon.lain.domain.repository.RecetaRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearRecetaViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: RecetaRepository,
    private val alimentoRepository: AlimentoRepository,
    private val usuarioRepository: UsuarioRepository
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
            repository.obtenerRecetaCompleta(id).take(1).collect { receta ->
                receta?.let { r ->
                    _state.update { 
                        it.copy(
                            nombre = r.nombre,
                            descripcion = r.descripcion,
                            pasosPreparacion = r.pasosPreparacion ?: "",
                            enlaceUrl = r.enlaceUrl ?: "",
                            ingredientesAñadidos = r.ingredientes,
                            kcalTotales = r.kcalPor100g, // Cuidado: Receta carga lo guardado por 100g
                            proteinasTotales = r.proteinasPor100g,
                            carbohidratosTotales = r.carbohidratosPor100g,
                            grasasTotales = r.grasasPor100g
                        )
                    }
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
        _state.update { it.copy(imagenByteArray = bytes) }
    }

    // --- BÚSQUEDA DE ALIMENTOS EN API ---

    fun onFiltroBusquedaCambiado(nuevoFiltro: String) {
        _state.update { it.copy(filtroBusqueda = nuevoFiltro) }
        // Si ya hay algo escrito, re-disparamos la búsqueda con el nuevo filtro
        val queryActual = _state.value.resultadosBusqueda.let { "" } // En una versión pro guardaríamos la query en el state
        // Para simplificar, si cambia el filtro, que el usuario tenga que escribir o capturamos la query
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
                val resultados = alimentoRepository.buscarAlimentos(query, tipoFinal)
                _state.update { it.copy(resultadosBusqueda = resultados, estaBuscando = false) }
            } catch (e: Exception) {
                _state.update { it.copy(estaBuscando = false) }
            }
        }
    }

    // --- LA MAGIA: GESTIÓN DE INGREDIENTES Y MACROS ---

    fun anadirIngrediente(alimentoBase: Alimento, gramos: Float) {
        viewModelScope.launch {
            // Persistimos el alimento en la BD local antes de usarlo en la receta
            // para garantizar integridad referencial (Foreign Key)
            alimentoRepository.guardarAlimentoLocal(alimentoBase)

            val nuevoIngrediente = Ingrediente(alimento = alimentoBase, cantidadEnGramos = gramos)

            _state.update { estadoActual ->
                val nuevaLista = estadoActual.ingredientesAñadidos + nuevoIngrediente

                estadoActual.copy(
                    ingredientesAñadidos = nuevaLista,
                    kcalTotales = nuevaLista.map { it.kcalTotales }.sum(),
                    proteinasTotales = nuevaLista.map { it.proteinasTotales }.sum(),
                    carbohidratosTotales = nuevaLista.map { it.carbohidratosTotales }.sum(),
                    grasasTotales = nuevaLista.map { it.grasasTotales }.sum()
                )
            }
        }
    }

    fun eliminarIngrediente(ingrediente: Ingrediente) {
        _state.update { estadoActual ->
            val nuevaLista = estadoActual.ingredientesAñadidos - ingrediente

            estadoActual.copy(
                ingredientesAñadidos = nuevaLista,
                kcalTotales = nuevaLista.map { it.kcalTotales }.sum(),
                proteinasTotales = nuevaLista.map { it.proteinasTotales }.sum(),
                carbohidratosTotales = nuevaLista.map { it.carbohidratosTotales }.sum(),
                grasasTotales = nuevaLista.map { it.grasasTotales }.sum()
            )
        }
    }

    // --- GENERADOR DE IDs COMPATIBLE CON KMP ---
    private fun generarIdUnico(): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "RECETA_" + (1..16)
            .map { caracteres.random() }
            .joinToString("")
    }

    // --- GUARDADO FINAL ---

    fun guardarReceta() {
        val estadoActual = _state.value

        if (estadoActual.nombre.isBlank() || estadoActual.ingredientesAñadidos.isEmpty()) return

        _state.update { it.copy(estaGuardando = true) }

        viewModelScope.launch {
            // Obtenemos el ID del usuario activo de verdad
            val usuarioActual = usuarioRepository.obtenerUsuarioActivo().firstOrNull()
            val finalUsuarioId = usuarioActual?.id ?: "usuario_anonimo"

            // Cálculo de macros por 100g
            val pesoTotal = estadoActual.ingredientesAñadidos.sumOf { it.cantidadEnGramos.toDouble() }.toFloat()
            val factor100g = if (pesoTotal > 0) 100f / pesoTotal else 0f

            val nuevaReceta = Receta(
                // Si estamos editando, mantenemos el ID original
                id = recetaId ?: generarIdUnico(),
                nombre = estadoActual.nombre,
                descripcion = estadoActual.descripcion,
                pasosPreparacion = estadoActual.pasosPreparacion,
                enlaceUrl = estadoActual.enlaceUrl,
                usuarioId = finalUsuarioId,
                ingredientes = estadoActual.ingredientesAñadidos,
                // Guardamos los macros calculados para 100g
                kcalPor100g = estadoActual.kcalTotales * factor100g,
                proteinasPor100g = estadoActual.proteinasTotales * factor100g,
                carbohidratosPor100g = estadoActual.carbohidratosTotales * factor100g,
                grasasPor100g = estadoActual.grasasTotales * factor100g
            )

            repository.guardarReceta(nuevaReceta)

            _state.update { it.copy(estaGuardando = false, guardadoExitoso = true) }
        }
    }
}