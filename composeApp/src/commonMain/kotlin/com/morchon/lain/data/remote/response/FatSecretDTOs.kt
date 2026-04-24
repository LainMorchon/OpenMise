package com.morchon.lain.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FatSecretTokenResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
    @SerialName("expires_in") val expiresIn: Int
)

@Serializable
data class FatSecretSearchResponse(
    val foods: FoodSearchContainer? = null
)

@Serializable
data class FoodSearchContainer(
    val food: List<FoodDto>? = emptyList(),
    @SerialName("max_results") val maxResults: String? = null,
    @SerialName("page_number") val pageNumber: String? = null,
    @SerialName("total_results") val totalResults: String? = null
)

@Serializable
data class FoodDto(
    @SerialName("food_id") val foodId: String,
    @SerialName("food_name") val foodName: String,
    @SerialName("food_type") val foodType: String,
    @SerialName("food_description") val foodDescription: String? = null,
    @SerialName("food_url") val foodUrl: String? = null
)
