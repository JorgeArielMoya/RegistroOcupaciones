package edu.ucne.registroocupaciones.presentation.horasextras.edit

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import java.time.LocalDate

data class HoraExtraFormUiState(
    val horaExtraId: Int? = null,
    val empleadoId: Int? = null,
    val empleados: List<Empleado> = emptyList(),
    val fechaDesde: LocalDate = LocalDate.now(),
    val fechaHasta: LocalDate = LocalDate.now(),
    val horasTotales: String = "",
    val horasNocturnas: String = "",
    val empleadoIdError: String? = null,
    val fechaDesdeError: String? = null,
    val fechaHastaError: String? = null,
    val horasTotalesError: String? = null,
    val horasNocturnasError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false
)