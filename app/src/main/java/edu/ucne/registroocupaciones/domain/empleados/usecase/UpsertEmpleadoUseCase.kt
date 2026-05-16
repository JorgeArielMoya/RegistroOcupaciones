package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import javax.inject.Inject

class UpsertEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    suspend operator fun invoke(empleado: Empleado): Result<Int> {

        val fechaResult = validateFechaIngreso(empleado.fechaIngreso)
        if (!fechaResult.isValid)
            return Result.failure(IllegalArgumentException(fechaResult.error))

        val nombresResult = validateNombres(empleado.nombres)
        if (!nombresResult.isValid)
            return Result.failure(IllegalArgumentException(nombresResult.error))

        val sueldoResult = validateSueldo(empleado.sueldo.toString())
        if (!sueldoResult.isValid)
            return Result.failure(IllegalArgumentException(sueldoResult.error))

        return runCatching { repository.upsert(empleado) }
    }
}