package edu.ucne.registroocupaciones.presentation.horasextras.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.DeleteHoraExtraUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.ObserveHoraExtraUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoraExtraListViewModel @Inject constructor(
    private val observeHorasExtrasUseCase: ObserveHoraExtraUseCase,
    private val deleteHoraExtraUseCase: DeleteHoraExtraUseCase,
    private val observeEmpleadosUseCase: ObserveEmpleadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HoraExtraListUiState(isLoading = true))
    val state: StateFlow<HoraExtraListUiState> = _state.asStateFlow()

    init {
        loadHorasExtras()
        loadEmpleados()
    }

    fun onEvent(event: HoraExtraListUiEvent) {
        when (event) {
            HoraExtraListUiEvent.Load -> loadHorasExtras()
            HoraExtraListUiEvent.Refresh -> loadHorasExtras()
            is HoraExtraListUiEvent.Delete -> onDelete(event.id)
            is HoraExtraListUiEvent.ShowMessage -> _state.update { it.copy(message = event.message) }
            HoraExtraListUiEvent.ClearMessage -> _state.update { it.copy(message = null) }
            HoraExtraListUiEvent.CreateNew -> _state.update { it.copy(navigateToCreate = true) }
            is HoraExtraListUiEvent.Edit -> _state.update { it.copy(navigateToEditId = event.id) }
        }
    }

    private fun loadEmpleados() {
        viewModelScope.launch {
            observeEmpleadosUseCase().collectLatest { list ->
                _state.update { it.copy(empleados = list) }
            }
        }
    }

    private fun loadHorasExtras() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeHorasExtrasUseCase().collectLatest { list ->
                _state.update { it.copy(isLoading = false, horasExtras = list, message = null) }
            }
        }
    }

    private fun onDelete(id: Int) {
        viewModelScope.launch {
            deleteHoraExtraUseCase(id)
            onEvent(HoraExtraListUiEvent.ShowMessage("Eliminado"))
        }
    }
}