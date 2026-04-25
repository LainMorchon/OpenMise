package com.morchon.lain.data.repository

import com.morchon.lain.data.remote.FatSecretApiService
import com.morchon.lain.data.mapper.aAlimentoEntity
import com.morchon.lain.domain.model.Alimento
import com.morchon.lain.domain.repository.AlimentoRepository

class AlimentoRepositoryImpl(
    private val apiService: FatSecretApiService,
    private val alimentoDao: com.morchon.lain.data.database.dao.AlimentoDao
) : AlimentoRepository {

    override suspend fun guardarAlimentoLocal(alimento: Alimento) {
        alimentoDao.insertarAlimento(alimento.aAlimentoEntity())
    }

    override suspend fun buscarAlimentos(query: String, type: String): List<Alimento> {
        val response = apiService.buscarAlimentos(query, type)
        
        return response.foods?.food?.map { dto ->
            val (kcal, prot, carbs, fats) = extraerMacrosDeDescripcion(dto.foodDescription ?: "")
            
            Alimento(
                id = dto.foodId,
                nombre = dto.foodName,
                origen = if (dto.foodType == "Brand") "API_COMMERCIAL" else "API_RAW",
                kcalPor100g = kcal,
                proteinasPor100g = prot,
                carbohidratosPor100g = carbs,
                grasasPor100g = fats
            )
        } ?: emptyList()
    }

    private fun extraerMacrosDeDescripcion(description: String): List<Float> {
        fun encontrarValor(regex: Regex): Float {
            return regex.find(description)?.groupValues?.get(1)?.toFloatOrNull() ?: 0f
        }

        val kcal = encontrarValor("Calories: ([\\d.]+)(?:kcal)?".toRegex())
        val fat = encontrarValor("Fat: ([\\d.]+)(?:g)?".toRegex())
        val carbs = encontrarValor("Carbs: ([\\d.]+)(?:g)?".toRegex())
        val prot = encontrarValor("Protein: ([\\d.]+)(?:g)?".toRegex())

        return listOf(kcal, prot, carbs, fat)
    }
}
