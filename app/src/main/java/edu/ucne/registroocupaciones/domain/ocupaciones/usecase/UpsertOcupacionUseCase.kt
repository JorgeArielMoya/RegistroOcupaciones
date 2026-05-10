package edu.ucne.registroocupaciones.domain.ocupaciones.usecase

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.repository.OcupacionRepository
import javax.inject.Inject

class UpsertOcupacionUseCase @Inject constructor(
    private val repository: OcupacionRepository
) {
    suspend operator fun invoke(ocupacion: Ocupacion): Result<Int> {
        val descripcionResult = validateDescripcion(ocupacion.descripcion)
        if (!descripcionResult.isValid) {
            return Result.failure(IllegalArgumentException(descripcionResult.error))
        }

        val sueldoResult = validateSueldo(ocupacion.sueldo.toString())
        if (!sueldoResult.isValid) {
            return Result.failure(IllegalArgumentException(sueldoResult.error))
        }

        return runCatching { repository.upsert(ocupacion) }
    }
}