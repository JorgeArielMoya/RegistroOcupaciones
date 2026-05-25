package edu.ucne.registroocupaciones.domain.horasextras.usecase

import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.repository.HoraExtraRepository
import javax.inject.Inject

class UpsertHoraExtraUseCase @Inject constructor(
    private val repository: HoraExtraRepository
){
    suspend operator fun invoke(horaExtra: HoraExtra) : Result<Int> {
        val empleadoIdResult = validateEmpleadoId(horaExtra.empleadoId)
        if(!empleadoIdResult.isValid){
            return Result.failure(IllegalArgumentException(empleadoIdResult.error))
        }

        val fechaDesdeResult = validateFechaDesde(horaExtra.fechaDesde)
        if (!fechaDesdeResult.isValid){
            return Result.failure(IllegalArgumentException(fechaDesdeResult.error))
        }

        val fechaHastaResult = validateFechaHasta(horaExtra.fechaDesde, horaExtra.fechaHasta)
        if (!fechaHastaResult.isValid){
            return Result.failure(IllegalArgumentException(fechaHastaResult.error))
        }

        val horasTotalesResult = validateHorasTotales(horaExtra.horasTotales)
        if(!horasTotalesResult.isValid){
            return Result.failure(IllegalArgumentException(horasTotalesResult.error))
        }

        val horasNocturnasResult = validateHorasNocturnas(horaExtra.horasNocturnas, horaExtra.horasTotales)
        if (!horasNocturnasResult.isValid){
            return Result.failure(IllegalArgumentException(horasNocturnasResult.error))
        }

        return runCatching { repository.upsert(horaExtra) }
    }
}