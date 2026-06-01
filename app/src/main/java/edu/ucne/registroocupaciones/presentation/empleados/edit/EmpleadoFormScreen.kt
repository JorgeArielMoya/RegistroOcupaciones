package edu.ucne.registroocupaciones.presentation.empleados.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoFormScreen(
    viewModel: EmpleadoFormViewModel = hiltViewModel(),
    onBack: () -> Unit,
    isPanel: Boolean = false,
    empleadoId: Int? = null
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    LaunchedEffect(empleadoId) {
        if (isPanel && empleadoId != null) {
            viewModel.onEvent(EmpleadoFormUiEvent.Load(empleadoId))
        }
    }

    if (isPanel) {
        EmpleadoFormContent(
            state = state,
            onEvent = viewModel::onEvent,
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (state.isNew) "Nuevo Empleado" else "Editar Empleado") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                EmpleadoFormContent(
                    state = state,
                    onEvent = viewModel::onEvent,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoFormContent(
    state: EmpleadoFormUiState,
    onEvent: (EmpleadoFormUiEvent) -> Unit,
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        OutlinedTextField(
            value = state.nombres,
            onValueChange = { onEvent(EmpleadoFormUiEvent.NombresChanged(it)) },
            label = { Text("Nombres") },
            modifier = Modifier.fillMaxWidth().testTag("input_nombres"),
            isError = state.nombresError != null,
            supportingText = state.nombresError?.let { { Text(it) } },
            singleLine = true
        )

        Box {
            OutlinedTextField(
                value = state.sexo.ifBlank { "Seleccionar sexo" },
                onValueChange = {},
                readOnly = true,
                label = { Text("Sexo") },
                trailingIcon = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Abrir opciones")
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("input_sexo"),
                isError = state.sexoError != null,
                supportingText = state.sexoError?.let { { Text(it) } }
            )
            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                listOf("Masculino", "Femenino", "Otro").forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onEvent(EmpleadoFormUiEvent.SexoChanged(opcion))
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Box {
            OutlinedTextField(
                value = state.fechaIngreso,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha de Ingreso") },
                placeholder = { Text("Selecciona la fecha") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar fecha")
                    }
                },
                modifier = Modifier.fillMaxWidth().testTag("input_fecha_ingreso"),
                isError = state.fechaIngresoError != null,
                supportingText = state.fechaIngresoError?.let { { Text(it) } }
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val localDate = java.time.Instant
                                .ofEpochMilli(selectedDateMillis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()
                            onEvent(EmpleadoFormUiEvent.FechaIngresoChanged(localDate.toString()))
                        }
                        showDatePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        OutlinedTextField(
            value = state.sueldo,
            onValueChange = { onEvent(EmpleadoFormUiEvent.SueldoChanged(it)) },
            label = { Text("Sueldo") },
            modifier = Modifier.fillMaxWidth().testTag("input_sueldo"),
            isError = state.sueldoError != null,
            supportingText = state.sueldoError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Button(
            onClick = { onEvent(EmpleadoFormUiEvent.Save) },
            modifier = Modifier.fillMaxWidth().testTag("btn_save"),
            enabled = !state.isSaving
        ) {
            if (state.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Guardar")
            }
        }

        if (!state.isNew) {
            OutlinedButton(
                onClick = { onEvent(EmpleadoFormUiEvent.Delete) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isDeleting,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                if (state.isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Text("Eliminar")
                }
            }
        }
    }
}