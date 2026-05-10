package edu.ucne.registroocupaciones.presentation.ocupaciones.list

sealed class OcupacionListUiEvent {
    object Load : OcupacionListUiEvent()
    object Refresh : OcupacionListUiEvent()
    data class Delete(val id: Int) : OcupacionListUiEvent()
    data class ShowMessage(val message: String) : OcupacionListUiEvent()
    object ClearMessage : OcupacionListUiEvent()
    object CreateNew : OcupacionListUiEvent()
    data class Edit(val id: Int) : OcupacionListUiEvent()
}