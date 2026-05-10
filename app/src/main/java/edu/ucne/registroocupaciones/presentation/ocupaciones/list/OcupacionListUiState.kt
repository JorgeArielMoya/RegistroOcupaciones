package edu.ucne.registroocupaciones.presentation.ocupaciones.list

import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion


data class OcupacionListUiState(
    val isLoading: Boolean = false,
    val ocupaciones: List<Ocupacion> = emptyList(),
    val message: String? = null,
    val navigateToCreate: Boolean = false,
    val navigateToEditId: Int? = null,
    val error: String? = null
)