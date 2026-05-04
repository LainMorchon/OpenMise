package com.morchon.lain.data.database.dao

import androidx.room.*
import com.morchon.lain.data.database.entity.ItemPlanEntity
import com.morchon.lain.data.database.entity.PlanEntity
import com.morchon.lain.data.database.model.PlanCompleto
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPlan(plan: PlanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarItemsPlan(items: List<ItemPlanEntity>)

    @Transaction
    @Query("SELECT * FROM Plan WHERE usuario_id = :usuarioId")
    fun obtenerPlanesCompletosPorUsuario(usuarioId: String): Flow<List<PlanCompleto>>

    @Transaction
    @Query("SELECT * FROM Plan WHERE id = :planId")
    suspend fun obtenerPlanCompletoPorId(planId: Long): PlanCompleto?

    @Query("DELETE FROM Plan WHERE id = :planId")
    suspend fun eliminarPlan(planId: Long)

    @Query("DELETE FROM Item_Plan WHERE plan_id = :planId")
    suspend fun eliminarItemsPorPlan(planId: Long)

    /**
     * Guarda un plan completo con sus ítems de forma atómica.
     */
    @Transaction
    suspend fun guardarPlanCompleto(plan: PlanEntity, items: List<ItemPlanEntity>): Long {
        val planId = insertarPlan(plan)
        // Si es una edición, limpiamos los items anteriores para evitar duplicados o huérfanos
        if (plan.id != 0L) {
            eliminarItemsPorPlan(plan.id)
        }
        val itemsConId = items.map { it.copy(plan_id = planId) }
        insertarItemsPlan(itemsConId)
        return planId
    }
}
