package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHoraExtraUseCase @Inject constructor(
    private val repository : HoraExtraRepository
) {
    operator fun invoke() : Flow<List<HoraExtra>> = repository.observeAll()
}