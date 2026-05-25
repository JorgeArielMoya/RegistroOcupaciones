package edu.ucne.registroocupaciones.presentation.horasextras.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.empleados.usecase.ObserveEmpleadoUseCase
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.domain.horasextras.usecase.DeleteHoraExtraUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.GetHoraExtraUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.UpsertHoraExtraUseCase
import edu.ucne.registroocupaciones.domain.horasextras.usecase.validateEmpleadoId
import edu.ucne.registroocupaciones.domain.horasextras.usecase.validateFechaDesde
import edu.ucne.registroocupaciones.domain.horasextras.usecase.validateFechaHasta
import edu.ucne.registroocupaciones.domain.horasextras.usecase.validateHorasTotales
import edu.ucne.registroocupaciones.domain.horasextras.usecase.validateHorasNocturnas
import edu.ucne.registroocupaciones.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HoraExtraFormViewModel @Inject constructor(
    private val getHoraExtraUseCase: GetHoraExtraUseCase,
    private val upsertHoraExtraUseCase: UpsertHoraExtraUseCase,
    private val deleteHoraExtraUseCase: DeleteHoraExtraUseCase,
    private val observeEmpleadosUseCase: ObserveEmpleadoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val routeArgs = savedStateHandle.toRoute<Screen.HoraExtraForm>()
    private val horaExtraId: Int = routeArgs.horaExtraId

    private val _state = MutableStateFlow(HoraExtraFormUiState())
    val state: StateFlow<HoraExtraFormUiState> = _state.asStateFlow()

    init {
        loadEmpleados()
        loadHoraExtra(horaExtraId)
    }

    fun onEvent(event: HoraExtraFormUiEvent) {
        when (event) {
            is HoraExtraFormUiEvent.Load -> loadHoraExtra(event.id)
            is HoraExtraFormUiEvent.EmpleadoIdChanged -> _state.update {
                it.copy(empleadoId = event.value, empleadoIdError = null)
            }
            is HoraExtraFormUiEvent.FechaDesdeChanged -> _state.update {
                it.copy(fechaDesde = event.value, fechaDesdeError = null)
            }
            is HoraExtraFormUiEvent.FechaHastaChanged -> _state.update {
                it.copy(fechaHasta = event.value, fechaHastaError = null)
            }
            is HoraExtraFormUiEvent.HorasTotalesChanged -> _state.update {
                it.copy(horasTotales = event.value, horasTotalesError = null)
            }
            is HoraExtraFormUiEvent.HorasNocturnasChanged -> _state.update {
                it.copy(horasNocturnas = event.value, horasNocturnasError = null)
            }
            HoraExtraFormUiEvent.Save -> onSave()
            HoraExtraFormUiEvent.Delete -> onDelete()
        }
    }

    private fun loadEmpleados() {
        viewModelScope.launch {
            observeEmpleadosUseCase().collectLatest { list ->
                _state.update { it.copy(empleados = list) }
            }
        }
    }

    private fun loadHoraExtra(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, horaExtraId = null) }
            return
        }
        viewModelScope.launch {
            val horaExtra = getHoraExtraUseCase(id)
            if (horaExtra != null) {
                _state.update {
                    it.copy(
                        isNew = false,
                        horaExtraId = horaExtra.horaExtraId,
                        empleadoId = horaExtra.empleadoId,
                        fechaDesde = horaExtra.fechaDesde,
                        fechaHasta = horaExtra.fechaHasta,
                        horasTotales = horaExtra.horasTotales.toString(),
                        horasNocturnas = horaExtra.horasNocturnas.toString()
                    )
                }
            } else {
                _state.update { it.copy(isNew = true, horaExtraId = null) }
            }
        }
    }

    private fun onSave() {
        val empleadoIdValidation  = validateEmpleadoId(state.value.empleadoId ?: 0)
        val fechaDesdeValidation  = validateFechaDesde(state.value.fechaDesde)
        val fechaHastaValidation  = validateFechaHasta(state.value.fechaDesde, state.value.fechaHasta)
        val horasTotalesValidation = validateHorasTotales(
            state.value.horasTotales.toDoubleOrNull() ?: 0.0
        )
        val horasNocturnasValidation = validateHorasNocturnas(
            state.value.horasNocturnas.toDoubleOrNull() ?: 0.0,
            state.value.horasTotales.toDoubleOrNull() ?: 0.0
        )

        if (!empleadoIdValidation.isValid || !fechaDesdeValidation.isValid ||
            !fechaHastaValidation.isValid || !horasTotalesValidation.isValid ||
            !horasNocturnasValidation.isValid
        ) {
            _state.update {
                it.copy(
                    empleadoIdError   = empleadoIdValidation.error,
                    fechaDesdeError   = fechaDesdeValidation.error,
                    fechaHastaError   = fechaHastaValidation.error,
                    horasTotalesError = horasTotalesValidation.error,
                    horasNocturnasError = horasNocturnasValidation.error
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val horaExtra = HoraExtra(
                horaExtraId   = state.value.horaExtraId ?: 0,
                empleadoId    = state.value.empleadoId ?: 0,
                fechaDesde    = state.value.fechaDesde,
                fechaHasta    = state.value.fechaHasta,
                horasTotales  = state.value.horasTotales.toDouble(),
                horasNocturnas = state.value.horasNocturnas.toDoubleOrNull() ?: 0.0
            )

            val result = upsertHoraExtraUseCase(horaExtra)
            result.onSuccess { newId ->
                _state.update {
                    it.copy(isSaving = false, saved = true, horaExtraId = newId, isNew = false)
                }
            }
            result.onFailure {
                _state.update { it.copy(isSaving = false) }
            }
        }
    }

    private fun onDelete() {
        val id = state.value.horaExtraId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteHoraExtraUseCase(id)
            _state.update { it.copy(isDeleting = false, deleted = true) }
        }
    }
}