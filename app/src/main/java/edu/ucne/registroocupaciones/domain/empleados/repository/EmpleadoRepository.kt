package edu.ucne.registroocupaciones.domain.empleados.repository

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import kotlinx.coroutines.flow.Flow

interface EmpleadoRepository {
    suspend fun upsert(empleado : Empleado) : Int
    suspend fun deleteEmpleado (id : Int)
    suspend fun getEmpleado (id : Int) : Empleado?
    fun observeAll () : Flow<List<Empleado>>
}