package com.morchon.lain.data.remote

import com.morchon.lain.core.config.FatSecretConfig
import com.morchon.lain.data.remote.response.FatSecretSearchResponse
import com.morchon.lain.data.remote.response.FatSecretTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import io.ktor.util.encodeBase64

/**
 * Servicio encargado de la comunicación directa con la API de FatSecret.
 * Gestiona la autenticación mediante OAuth2 y las peticiones de búsqueda.
 */
class FatSecretApiService(private val client: HttpClient) {

    private var cachedToken: String? = null

    /**
     * Obtiene un token de acceso válido.
     * Si no hay uno cacheado, lo solicita a FatSecret usando las credenciales del cliente.
     */
    private suspend fun getAccessToken(): String {
        cachedToken?.let { return it }

        // Las credenciales deben enviarse en formato Base64 según el estándar OAuth2
        val credentials = "${FatSecretConfig.CLIENT_ID}:${FatSecretConfig.CLIENT_SECRET}"
        val encodedCredentials = credentials.encodeBase64()

        val response: FatSecretTokenResponse = client.submitForm(
            url = FatSecretConfig.TOKEN_URL,
            formParameters = parameters {
                append("grant_type", "client_credentials")
                append("scope", "basic")
            }
        ) {
            header("Authorization", "Basic $encodedCredentials")
        }.body()

        cachedToken = response.accessToken
        return response.accessToken
    }

    /**
     * Busca alimentos por nombre en la base de datos de FatSecret.
     * @param query El término de búsqueda (ej: "Manzana").
     * @param pageNumber El número de página para la paginación.
     */
    suspend fun buscarAlimentos(query: String, pageNumber: Int = 0): FatSecretSearchResponse {
        val token = getAccessToken()

        return client.get(FatSecretConfig.BASE_URL) {
            header("Authorization", "Bearer $token")
            parameter("method", "foods.search")
            parameter("search_expression", query)
            parameter("page_number", pageNumber)
            parameter("format", "json")
        }.body()
    }

    /**
     * Obtiene los detalles nutricionales específicos de un alimento mediante su ID.
     * FatSecret en la búsqueda devuelve datos limitados, por lo que este método
     * es vital para obtener los macros exactos por cada 100g.
     */
    suspend fun obtenerDetallesAlimento(foodId: String) {
        // Implementaremos el DTO de detalles en el siguiente paso si es necesario
        // por ahora nos centramos en la búsqueda básica.
    }
}
