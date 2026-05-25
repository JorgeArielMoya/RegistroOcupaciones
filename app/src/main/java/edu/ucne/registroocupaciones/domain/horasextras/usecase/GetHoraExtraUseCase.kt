package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import javax.inject.Inject

class GetHoraExtraUseCase @Inject constructor(
    private val repository : HoraExtraRepository
) {
    suspend operator fun invoke(id : Int) : HoraExtra? = repository.getHoraExtra(id)
}