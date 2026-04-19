package com.morchon.lain.ui.recetas.crear

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Ingrediente
import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.RecetaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class CrearRecetaViewModel(
    private val repository: RecetaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CrearRecetaState())
    val state: StateFlow<CrearRecetaState> = _state.asStateFlow()

    // --- FUNCIONES PARA ACTUALIZAR EL FORMULARIO ---

    fun onNombreCambiado(nuevoNombre: String) {
        _state.update { it.copy(nombre = nuevoNombre) }
    }

    fun onDescripcionCambiada(nuevaDesc: String) {
        _state.update { it.copy(descripcion = nuevaDesc) }
    }

    // --- LA MAGIA: GESTIÓN DE INGREDIENTES Y MACROS ---

    fun anadirIngrediente(alimentoBase: Alimento, gramos: Float) {
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
            val nuevaReceta = Receta(
                // Usamos nuestra función KMP en lugar del UUID de Java
                id = generarIdUnico(),
                nombre = estadoActual.nombre,
                descripcion = estadoActual.descripcion,
                usuarioId = "usuario_actual",
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