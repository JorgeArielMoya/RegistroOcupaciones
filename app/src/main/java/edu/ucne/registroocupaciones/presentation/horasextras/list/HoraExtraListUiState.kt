package edu.ucne.registroocupaciones.presentation.horasextras.list

import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra

data class HoraExtraListUiState(
    val isLoading: Boolean = false,
    val horasExtras: List<HoraExtra> = emptyList(),
    val empleados: List<Empleado> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
    val error: String? = null
)