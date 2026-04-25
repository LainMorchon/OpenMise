package com.morchon.lain.data.remote

import com.morchon.lain.core.config.FatSecretConfig
import com.morchon.lain.data.remote.response.FatSecretSearchResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Servicio encargado de la comunicación con el Proxy de OpenMise.
 * El proxy gestiona la autenticación con FatSecret y oculta las credenciales.
 */
class FatSecretApiService(private val client: HttpClient) {

    /**
     * Busca alimentos por nombre a través del servidor proxy.
     * @param query El término de búsqueda (ej: "Manzana").
     */
    suspend fun buscarAlimentos(query: String): FatSecretSearchResponse {
        return client.get("${FatSecretConfig.BASE_URL}/search") {
            parameter("q", query)
        }.body()
    }

    /**
     * Obtiene los detalles nutricionales específicos de un alimento mediante su ID.
     * Pendiente de implementar en el proxy si es necesario.
     */
    suspend fun obtenerDetallesAlimento(foodId: String) {
        // TODO: Implementar cuando el proxy soporte detalles
    }
}
