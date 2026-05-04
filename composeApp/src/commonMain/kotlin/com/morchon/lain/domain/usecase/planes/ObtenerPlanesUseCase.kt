package com.morchon.lain.domain.usecase.planes

import com.morchon.lain.domain.model.Plan
import com.morchon.lain.domain.repository.PlanRepository
import com.morchon.lain.domain.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObtenerPlanesUseCase(
    private val planRepository: PlanRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Plan>> {
        return usuarioRepository.obtenerUsuarioActivo().flatMapLatest { usuario ->
            usuario?.let {
                planRepository.obtenerPlanesPorUsuario(it.id)
            } ?: flowOf(emptyList())
        }
    }
}
