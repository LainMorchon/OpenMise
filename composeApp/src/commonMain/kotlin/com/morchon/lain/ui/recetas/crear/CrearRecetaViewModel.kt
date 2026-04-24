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
    }

    // --- FUNCIONES PARA ACTUALIZAR EL FORMULARIO ---

    fun onNombreCambiado(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre) }
    }

    fun onDescripcionCambiada(nuevaDesc: String) {
        _state.update { it.copy(descripcion = nuevaDesc) }
    }

    // --- BÚSQUEDA DE ALIMENTOS EN API ---

    fun buscarAlimento(query: String) {
        busquedaJob?.cancel() // Cancelamos la búsqueda anterior si el usuario sigue escribiendo

        if (query.isBlank()) {
            _state.update { it.copy(resultadosBusqueda = emptyList(), estaBuscando = false) }
            return
        }

        busquedaJob = viewModelScope.launch {
            delay(500) // Debounce: esperamos medio segundo antes de disparar la petición
            _state.update { it.copy(estaBuscando = true) }
            
            try {
                val resultados = alimentoRepository.buscarAlimentos(query)
                _state.update { it.copy(resultadosBusqueda = resultados, estaBuscando = false) }
            } catch (e: Exception) {
                _state.update { it.copy(estaBuscando = false) }
                // Aquí podrías gestionar el error (ej: mostrar un snackbar)
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
            val usuarioId = usuarioActual?.id ?: "usuario_anonimo"

            val nuevaReceta = Receta(
                // Si estamos editando, mantenemos el ID original
                id = recetaId ?: generarIdUnico(),
                nombre = estadoActual.nombre,
                descripcion = estadoActual.descripcion,
                usuarioId = usuarioId,
                ingredientes = estadoActual.ingredientesAñadidos,
                kcalPor100g = estadoActual.kcalTotales,
                proteinasPor100g = estadoActual.proteinasTotales,
                carbohidratosPor100g = estadoActual.carbohidratosTotales,
                grasasPor100g = estadoActual.grasasTotales
            )

            repository.guardarReceta(nuevaReceta)

            _state.update { it.copy(estaGuardando = false, guardadoExitoso = true) }
        }
    }
}