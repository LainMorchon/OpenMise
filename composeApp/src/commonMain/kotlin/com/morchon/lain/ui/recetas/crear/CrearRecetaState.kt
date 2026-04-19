package com.morchon.lain.ui.recetas.crear

import com.morchon.lain.domain.model.Ingrediente

data class CrearRecetaState(
    // 1. Textos del formulario
    val nombre: String = "",
    val descripcion: String = "",

    // 2. La lista de ingredientes que el usuario va añadiendo
    val ingredientesAñadidos: List<Ingrediente> = emptyList(),

    // 3. Macros totales (se calcularán solos en el ViewModel)
    val kcalTotales: Float = 0f,
    val proteinasTotales: Float = 0f,
    val carbohidratosTotales: Float = 0f,
    val grasasTotales: Float = 0f,

    // 4. Control de UI
    val estaGuardando: Boolean = false,
    val guardadoExitoso: Boolean = false // Para saber cuándo navegar hacia atrás
)