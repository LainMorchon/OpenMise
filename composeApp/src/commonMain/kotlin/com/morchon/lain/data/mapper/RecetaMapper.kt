package com.morchon.lain.data.mapper

import com.morchon.lain.data.database.entity.AlimentoEntity
import com.morchon.lain.data.database.entity.DetalleRecetaEntity
import com.morchon.lain.data.database.entity.IngredienteRecetaEntity
import com.morchon.lain.data.database.model.IngredienteConInfo
import com.morchon.lain.data.database.model.RecetaCompleta
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.Ingrediente
import com.morchon.lain.domain.model.Receta

// ====================================================================
// 1. DE ENTIDAD (ROOM) A DOMINIO (APP) -> Para Leer de la BD
// ====================================================================

/**
 * Convierte una entidad básica de Alimento a tu modelo de Dominio.
 */
fun AlimentoEntity.aDominio(): Alimento {
    return Alimento(
        id = id,
        nombre = nombre,
        origen = origen,
        kcalPor100g = kcal_por_100g,
        proteinasPor100g = proteinas_por_100g,
        carbohidratosPor100g = carbohidratos_por_100g,
        grasasPor100g = grasas_por_100g
    )
}

/**
 * Traduce el POJO relacional de Room a tu clase pura Receta.
 */
fun RecetaCompleta.aDominio(): Receta {
    return Receta(
        id = alimento.id,
        nombre = alimento.nombre,
        // 1. Borramos 'origen' porque Receta ya lo fuerza a "CUSTOM_RECIPE" internamente

        // 2. Añadimos los macros que el constructor de Receta nos exige (¡Se nos olvidaron!)
        kcalPor100g = alimento.kcal_por_100g,
        proteinasPor100g = alimento.proteinas_por_100g,
        carbohidratosPor100g = alimento.carbohidratos_por_100g,
        grasasPor100g = alimento.grasas_por_100g,

        // Propiedades del detalle
        usuarioId = detalle?.usuario_id ?: "usuario_desconocido",
        descripcion = detalle?.descripcion ?: "",
        pasosPreparacion = detalle?.pasos_preparacion,
        enlaceUrl = detalle?.enlace_url,

        // La lista de ingredientes ya no debería dar error fantasma
        ingredientes = ingredientes.map { it.aDominio() }
    )
}

/**
 * Traduce la relación Ingrediente-Alimento a tu modelo puro Ingrediente.
 */
fun IngredienteConInfo.aDominio(): Ingrediente {
    return Ingrediente(
        alimento = alimentoInfo.aDominio(),
        cantidadEnGramos = detalle.cantidad_en_gramos
    )
}

// ====================================================================
// 2. DE DOMINIO (APP) A ENTIDADES (ROOM) -> Para Guardar en la BD
// ====================================================================

/**
 * Extrae la cabecera (AlimentoEntity) de un Alimento genérico.
 */
fun Alimento.aAlimentoEntity(): AlimentoEntity {
    return AlimentoEntity(
        id = id,
        nombre = nombre,
        origen = origen,
        kcal_por_100g = kcalPor100g,
        proteinas_por_100g = proteinasPor100g,
        carbohidratos_por_100g = carbohidratosPor100g,
        grasas_por_100g = grasasPor100g
    )
}

/**
 * Extrae la cabecera (AlimentoEntity) de una Receta.
 * Aquí calculamos los macros totales para cachearlos en la tabla Alimento.
 */
fun Receta.aAlimentoEntity(): AlimentoEntity {
    return (this as Alimento).aAlimentoEntity()
}

/**
 * Extrae los detalles extendidos de una Receta.
 */
fun Receta.aDetalleEntity(): DetalleRecetaEntity {
    return DetalleRecetaEntity(
        alimento_id = id,
        usuario_id = usuarioId,
        descripcion = descripcion,
        pasos_preparacion = pasosPreparacion,
        enlace_url = enlaceUrl
    )
}

/**
 * Convierte la lista de ingredientes puros en entidades de tabla intermedia.
 */
fun Receta.aIngredientesEntities(): List<IngredienteRecetaEntity> {
    return ingredientes.map { ingrediente ->
        IngredienteRecetaEntity(
            // PRO-TIP KMP: Para evitar librerías externas de UUIDs,
            // creamos un ID determinista uniendo la receta y el alimento.
            id = "${this.id}_${ingrediente.alimento.id}",
            receta_id = this.id,
            ingrediente_id = ingrediente.alimento.id,
            cantidad_en_gramos = ingrediente.cantidadEnGramos
        )
    }
}