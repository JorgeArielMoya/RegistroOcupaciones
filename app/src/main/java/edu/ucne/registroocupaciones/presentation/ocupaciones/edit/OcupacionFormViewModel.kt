package edu.ucne.registroocupaciones.presentation.ocupaciones.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.DeleteOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.GetOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.UpsertOcupacionUseCase
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.validateDescripcion
import edu.ucne.registroocupaciones.domain.ocupaciones.usecase.validateSueldo
import edu.ucne.registroocupaciones.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcupacionFormViewModel @Inject constructor(
    private val getOcupacionUseCase: GetOcupacionUseCase,
    private val upsertOcupacionUseCase: UpsertOcupacionUseCase,
    private val deleteOcupacionUseCase: DeleteOcupacionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val routeArgs = savedStateHandle.toRoute<Screen.OcupacionForm>()
    private val ocupacionId: Int = routeArgs.ocupacionId

    private val _state = MutableStateFlow(OcupacionFormUiState())
    val state: StateFlow<OcupacionFormUiState> = _state.asStateFlow()

    init {
        loadOcupacion(ocupacionId)
    }

    fun onEvent(event: OcupacionFormUiEvent) {
        when (event) {
            is OcupacionFormUiEvent.Load -> loadOcupacion(event.id)
            is OcupacionFormUiEvent.DescripcionChanged -> _state.update {
                it.copy(descripcion = event.value, descripcionError = null, descripcionDuplicada = false )
            }
            is OcupacionFormUiEvent.SueldoChanged -> _state.update {
                it.copy(sueldo = event.value, sueldoError = null)
            }
            OcupacionFormUiEvent.Save -> onSave()
            OcupacionFormUiEvent.Delete -> onDelete()
        }
    }

    private fun loadOcupacion(id: Int?) {
        if (id == null || id == 0) {
            _state.update { it.copy(isNew = true, ocupacionId = null) }
            return
        }

        viewModelScope.launch {
            val ocupacion = getOcupacionUseCase(id)
            if (ocupacion != null) {
                _state.update {
                    it.copy(
                        isNew = false,
                        ocupacionId = ocupacion.ocupacionId,
                        descripcion = ocupacion.descripcion,
                        sueldo = ocupacion.sueldo.toString()
                    )
                }
            } else {
                _state.update { it.copy(isNew = true, ocupacionId = null) }
            }
        }
    }

    private fun onSave() {
        val descripcion = state.value.descripcion
        val descripcionValidation = validateDescripcion(descripcion)
        val sueldoValidation = validateSueldo(state.value.sueldo)

        if (!descripcionValidation.isValid || !sueldoValidation.isValid) {
            _state.update {
                it.copy(
                    descripcionError = descripcionValidation.error,
                    sueldoError = sueldoValidation.error
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }

            val ocupacion = Ocupacion(
                ocupacionId = state.value.ocupacionId ?: 0,
                descripcion = descripcion,
                sueldo = state.value.sueldo.toDouble()
            )

            val result = upsertOcupacionUseCase(ocupacion)
            result.onSuccess { newId ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        saved = true,
                        ocupacionId = newId,
                        isNew = false
                    )
                }
            }
                result.onFailure { error ->
                val isDuplicate = error.message?.contains("Ya existe una ocupación") == true
                _state.update {
                    it.copy(
                        isSaving = false,
                        descripcionDuplicada = isDuplicate
                    )
                }
            }
        }
    }

    private fun onDelete() {
        val id = state.value.ocupacionId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            deleteOcupacionUseCase(id)
            _state.update { it.copy(isDeleting = false, deleted = true) }
        }
    }
}