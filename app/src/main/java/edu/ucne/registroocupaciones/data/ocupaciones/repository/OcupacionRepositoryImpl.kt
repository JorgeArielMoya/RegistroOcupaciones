package edu.ucne.registroocupaciones.data.ocupaciones.repository

import edu.ucne.registroocupaciones.data.ocupaciones.local.dao.OcupacionDao
import edu.ucne.registroocupaciones.data.ocupaciones.mappers.toDomain
import edu.ucne.registroocupaciones.data.ocupaciones.mappers.toEntity
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OcupacionRepositoryImpl @Inject constructor(
    private val localDataSource: OcupacionDao
) : OcupacionRepository {

    override fun observeOcupaciones(): Flow<List<Ocupacion>> {
        return localDataSource.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getOcupacion(id: Int): Ocupacion? {
        return localDataSource.getById(id)?.toDomain()
    }

    override suspend fun upsert(ocupacion: Ocupacion): Int {
        localDataSource.upsert(ocupacion.toEntity())
        return ocupacion.ocupacionId
    }

    override suspend fun delete(id: Int) {
        localDataSource.deleteById(id)
    }

    override suspend fun existsByDescripcion(descripcion: String, excludeId: Int): Boolean {
        return localDataSource.existsByDescripcion(descripcion, excludeId)
    }
}