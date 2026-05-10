package edu.ucne.registroocupaciones.presentation.ocupaciones.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.ObserveOcupacionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcupacionListViewModel @Inject constructor(
    private val observeOcupacionesUseCase: ObserveOcupacionUseCase,
    private val deleteOcupacionUseCase: DeleteOcupacionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OcupacionListUiState(isLoading = true))
    val state: StateFlow<OcupacionListUiState> = _state.asStateFlow()

    init {
        loadOcupaciones()
    }

    fun onEvent(event: OcupacionListUiEvent) {
        when (event) {
            OcupacionListUiEvent.Load -> loadOcupaciones()
            OcupacionListUiEvent.Refresh -> loadOcupaciones()
            is OcupacionListUiEvent.Delete -> onDelete(event.id)
            is OcupacionListUiEvent.ShowMessage -> _state.update { it.copy(message = event.message) }
            OcupacionListUiEvent.ClearMessage -> _state.update { it.copy(message = null) }
            OcupacionListUiEvent.CreateNew -> _state.update { it.copy(navigateToCreate = true) }
            is OcupacionListUiEvent.Edit -> _state.update { it.copy(navigateToEditId = event.id) }
        }
    }

    fun loadOcupaciones() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            observeOcupacionesUseCase().collectLatest { list ->
                _state.update { it.copy(isLoading = false, ocupaciones = list, message = null) }
            }
        }
    }

    private fun onDelete(id: Int) {
        viewModelScope.launch {
            deleteOcupacionUseCase(id)
            onEvent(OcupacionListUiEvent.ShowMessage("Eliminado"))
        }
    }
}