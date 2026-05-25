package edu.ucne.registroocupaciones.presentation.horasextras.edit

import java.time.LocalDate

sealed interface HoraExtraFormUiEvent {
    data class Load(val id: Int?) : HoraExtraFormUiEvent
    data class EmpleadoIdChanged(val value: Int) : HoraExtraFormUiEvent
    data class FechaDesdeChanged(val value: LocalDate) : HoraExtraFormUiEvent
    data class FechaHastaChanged(val value: LocalDate) : HoraExtraFormUiEvent
    data class HorasTotalesChanged(val value: String) : HoraExtraFormUiEvent
    data class HorasNocturnasChanged(val value: String) : HoraExtraFormUiEvent
    data object Save : HoraExtraFormUiEvent
    data object Delete : HoraExtraFormUiEvent
}