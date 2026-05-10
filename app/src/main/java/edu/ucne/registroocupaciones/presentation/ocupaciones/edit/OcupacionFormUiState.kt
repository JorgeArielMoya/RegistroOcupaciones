package edu.ucne.registroocupaciones.presentation.ocupaciones.edit

data class OcupacionFormUiState(
    val ocupacionId: Int? = null,
    val descripcion: String = "",
    val sueldo: String = "",
    val descripcionError: String? = null,
    val sueldoError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isNew: Boolean = true,
    val saved: Boolean = false,
    val deleted: Boolean = false
)