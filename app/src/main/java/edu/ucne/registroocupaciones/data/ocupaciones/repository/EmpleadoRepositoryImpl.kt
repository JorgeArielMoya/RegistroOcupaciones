package edu.ucne.registroocupaciones.data.ocupaciones.repository

import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.EmpleadoDao
import edu.ucne.registroocupaciones.data.ocupaciones.mappers.toDomain
import edu.ucne.registroocupaciones.data.ocupaciones.mappers.toEntity
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmpleadoRepositoryImpl @Inject constructor(
    private val localDataSource : EmpleadoDao
) : EmpleadoRepository {
    override suspend fun upsert(empleado: Empleado): Int {
        localDataSource.upsert(empleado.toEntity())
        return empleado.empleadoId
    }

    override suspend fun delete(id: Int) {
        localDataSource.deleteById(id)
    }

    override suspend fun getEmpleado(id: Int): Empleado? {
        return localDataSource.getById(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<Empleado>> {
        return localDataSource.observeAll()
            .map { empleadoEntities -> empleadoEntities.map { it.toDomain() } }
    }
}