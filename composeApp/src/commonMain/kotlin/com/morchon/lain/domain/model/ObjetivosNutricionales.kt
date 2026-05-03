package com.morchon.lain.domain.model

/**
 * Representa las metas diarias de macronutrientes y calorías de un usuario.
 */
data class ObjetivosNutricionales(
    val kcal: Int = 2000,
    val proteinas: Int = 150,
    val carbohidratos: Int = 200,
    val grasas: Int = 65
)
