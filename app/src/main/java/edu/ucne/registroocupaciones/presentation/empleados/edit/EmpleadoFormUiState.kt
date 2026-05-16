package edu.ucne.registroocupaciones.presentation.empleados.edit

data class EmpleadoFormUiState(
    val empleadoId: Int? = null,
    val nombres: String = "",
    val sexo: String = "",
    val fechaIngreso: String = "",
    val sueldo: String = "",
    val nombresError: String? = null,
    val sexoError: String? = null,
    val fechaIngresoError: String? = null,
    val sueldoError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false
)