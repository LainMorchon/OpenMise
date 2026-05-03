package com.morchon.lain.domain.usecase.recetas

import com.morchon.lain.domain.model.Ingrediente
import com.morchon.lain.domain.model.Receta
import com.morchon.lain.domain.repository.RecetaRepository
import com.morchon.lain.ui.core.util.ImageManager

/**
 * Caso de uso para crear o actualizar una receta.
 * Se encarga de procesar la imagen, calcular los macros por 100g y generar el ID si es necesario.
 */
class GuardarRecetaUseCase(
    private val repository: RecetaRepository,
    private val imageManager: ImageManager?
) {

    suspend operator fun invoke(
        id: String?,
        nombre: String,
        descripcion: String,
        pasosPreparacion: String?,
        enlaceUrl: String?,
        usuarioId: String,
        ingredientes: List<Ingrediente>,
        imagenUrl: String?,
        imagenBytes: ByteArray? = null
    ): Result<Unit> {
        if (nombre.isBlank()) return Result.failure(Exception("El nombre es obligatorio"))
        if (ingredientes.isEmpty()) return Result.failure(Exception("La receta debe tener al menos un ingrediente"))

        // Orquestación: Procesar la imagen si hay nuevos bytes
        val rutaImagenFinal = imagenBytes?.let { bytes ->
            imageManager?.saveImage(bytes)
        } ?: imagenUrl

        // Cálculo de macros totales y peso total
        val kcalTotales = ingredientes.sumOf { it.kcalTotales.toDouble() }.toFloat()
        val proteinasTotales = ingredientes.sumOf { it.proteinasTotales.toDouble() }.toFloat()
        val carbohidratosTotales = ingredientes.sumOf { it.carbohidratosTotales.toDouble() }.toFloat()
        val grasasTotales = ingredientes.sumOf { it.grasasTotales.toDouble() }.toFloat()
        val pesoTotal = ingredientes.sumOf { it.cantidadEnGramos.toDouble() }.toFloat()

        // Cálculo de macros por 100g (Snapshot de la receta)
        val factor100g = if (pesoTotal > 0) 100f / pesoTotal else 0f

        val receta = Receta(
            id = id ?: generarIdUnico(),
            nombre = nombre,
            descripcion = descripcion,
            pasosPreparacion = pasosPreparacion,
            enlaceUrl = enlaceUrl,
            usuarioId = usuarioId,
            ingredientes = ingredientes,
            imagenUrl = rutaImagenFinal,
            kcalPor100g = kcalTotales * factor100g,
            proteinasPor100g = proteinasTotales * factor100g,
            carbohidratosPor100g = carbohidratosTotales * factor100g,
            grasasPor100g = grasasTotales * factor100g
        )

        return try {
            repository.guardarReceta(receta)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generarIdUnico(): String {
        val caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "RECETA_" + (1..16)
            .map { caracteres.random() }
            .joinToString("")
    }
}
