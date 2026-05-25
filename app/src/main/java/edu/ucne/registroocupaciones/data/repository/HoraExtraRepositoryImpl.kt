package edu.ucne.registroocupaciones.data.repository

import edu.ucne.registroocupaciones.data.local.dao.HoraExtraDao
import edu.ucne.registroocupaciones.data.mappers.toDomain
import edu.ucne.registroocupaciones.data.mappers.toEntity
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HoraExtraRepositoryImpl @Inject constructor(
    private val localDataSource : HoraExtraDao
) : HoraExtraRepository {
    override suspend fun upsert(horaExtra: HoraExtra): Int {
        localDataSource.upsert(horaExtra.toEntity())
        return horaExtra.horaExtraId
    }

    override suspend fun deleteHoraExtra(id: Int) {
        localDataSource.deleteById(id)
    }

    override suspend fun getHoraExtra(id: Int): HoraExtra? {
        return localDataSource.getById(id)?.toDomain()
    }

    override fun observeAll(): Flow<List<HoraExtra>> {
        return localDataSource.observeAll().map { entities -> entities.map { it.toDomain() } }
    }
}