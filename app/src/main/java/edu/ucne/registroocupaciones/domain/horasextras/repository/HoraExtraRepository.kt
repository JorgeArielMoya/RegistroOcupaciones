package edu.ucne.registroocupaciones.domain.horasextras.repository

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import kotlinx.coroutines.flow.Flow

interface HoraExtraRepository {
    suspend fun upsert (horaExtra: HoraExtra) : Int
    suspend fun deleteHoraExtra (id : Int)
    suspend fun getHoraExtra (id : Int) : HoraExtra?
    fun observeAll () : Flow<List<HoraExtra>>
}