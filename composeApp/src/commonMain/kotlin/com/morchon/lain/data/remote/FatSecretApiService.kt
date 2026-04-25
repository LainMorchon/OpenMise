package com.morchon.lain.data.remote

import com.morchon.lain.core.config.FatSecretConfig
import com.morchon.lain.data.remote.response.FatSecretSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Servicio encargado de la comunicación con el Proxy de OpenMise.
 */
class FatSecretApiService(private val client: HttpClient) {

    /**
     * Busca alimentos por nombre a través del servidor proxy.
     * @param query El término de búsqueda.
     * @param type El tipo de filtrado: "all", "generic" o "brand".
     */
    suspend fun buscarAlimentos(query: String, type: String = "all"): FatSecretSearchResponse {
        return client.get("${FatSecretConfig.BASE_URL}/search") {
            parameter("q", query)
            parameter("type", type)
        }.body()
    }

    suspend fun obtenerDetallesAlimento(foodId: String) {
        // Pendiente de implementar en el proxy
    }
}
