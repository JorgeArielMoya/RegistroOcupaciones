package edu.ucne.registroocupaciones.domain.empleados.usecase

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.empleados.repository.EmpleadoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEmpleadoUseCase @Inject constructor(
    private val repository: EmpleadoRepository
) {
    operator fun invoke() : Flow<List<Empleado>> = repository.observeAll()
}