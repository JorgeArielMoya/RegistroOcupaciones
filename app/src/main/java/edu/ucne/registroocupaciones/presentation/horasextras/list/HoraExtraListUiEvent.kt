package edu.ucne.registroocupaciones.presentation.horasextras.list

sealed class HoraExtraListUiEvent {
    object Load : HoraExtraListUiEvent()
    object Refresh : HoraExtraListUiEvent()
    data class Delete(val id: Int) : HoraExtraListUiEvent()
    data class ShowMessage(val message: String) : HoraExtraListUiEvent()
    object ClearMessage : HoraExtraListUiEvent()
    object CreateNew : HoraExtraListUiEvent()
    data class Edit(val id: Int) : HoraExtraListUiEvent()
}