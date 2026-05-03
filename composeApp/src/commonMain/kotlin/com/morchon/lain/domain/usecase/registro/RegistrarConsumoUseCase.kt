package com.morchon.lain.domain.usecase.registro

import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.model.MomentoComida
import com.morchon.lain.domain.model.RegistroDiario
import com.morchon.lain.domain.repository.RegistroDiarioRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Caso de uso para registrar un consumo en el diario.
 * Implementa el PATRÓN SNAPSHOT: copia los macros actuales del alimento.
 */
class RegistrarConsumoUseCase(
    private val registroRepository: RegistroDiarioRepository,
    private val usuarioRepository: UsuarioRepository,
    private val alimentoRepository: com.morchon.lain.domain.repository.AlimentoRepository
) {
    suspend operator fun invoke(
        alimento: Alimento,
        cantidadGramos: Double,
        momentoComida: MomentoComida
    ) {
        // 1. Persistimos el alimento en el catálogo local (caché de recientes/frecuentes)
        alimentoRepository.guardarAlimentoLocal(alimento)

        // 2. Registramos la ingesta
        val usuario = usuarioRepository.obtenerUsuarioActivo().first() ?: return
        val hoy = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        val registro = RegistroDiario(
            usuarioId = usuario.id,
            alimentoId = alimento.id,
            nombreAlimento = alimento.nombre,
            fecha = hoy,
            cantidadGramos = cantidadGramos,
            momentoComida = momentoComida,
            // SNAPSHOT: Calculamos y guardamos los macros en este preciso instante
            kcalHistoricas = alimento.calcularKcal(cantidadGramos),
            proteinasHistoricas = alimento.calcularProteinas(cantidadGramos),
            carbohidratosHistoricos = alimento.calcularCarbohidratos(cantidadGramos),
            grasasHistoricas = alimento.calcularGrasas(cantidadGramos)
        )

        registroRepository.guardarRegistro(registro)
    }
}
