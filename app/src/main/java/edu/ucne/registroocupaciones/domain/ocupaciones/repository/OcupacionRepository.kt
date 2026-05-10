package edu.ucne.registroocupaciones.domain.ocupaciones.repository

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import kotlinx.coroutines.flow.Flow

interface OcupacionRepository {
    fun observeOcupaciones(): Flow<List<Ocupacion>>
    suspend fun getOcupacion(id: Int): Ocupacion?
    suspend fun upsert(task: Ocupacion): Int
    suspend fun delete(id: Int)
    suspend fun existsByDescripcion(descripcion: String, excludeId: Int = 0): Boolean
}