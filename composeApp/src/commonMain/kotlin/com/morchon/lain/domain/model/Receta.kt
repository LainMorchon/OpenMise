package com.morchon.lain.domain.model

/**
 * Una Receta es un Alimento complejo que contiene una lista de ingredientes
 * y pasos de preparación.
 */
class Receta(
    id: String,
    nombre: String,
    // Macros cacheados por 100g de la receta ya terminada
    kcalPor100g: Float,
    proteinasPor100g: Float,
    carbohidratosPor100g: Float,
    grasasPor100g: Float,

    // Propiedades exclusivas de la Receta
    val usuarioId: String,
    val descripcion: String,
    val pasosPreparacion: String? = null,
    val enlaceUrl: String? = null,
    val ingredientes: List<Ingrediente> = emptyList()

) : Alimento(
    id = id,
    nombre = nombre,
    origen = "CUSTOM_RECIPE", // Forzamos el origen por defecto
    kcalPor100g = kcalPor100g,
    proteinasPor100g = proteinasPor100g,
    carbohidratosPor100g = carbohidratosPor100g,
    grasasPor100g = grasasPor100g
)