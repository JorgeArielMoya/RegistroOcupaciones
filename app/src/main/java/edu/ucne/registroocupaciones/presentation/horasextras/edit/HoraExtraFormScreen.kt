package edu.ucne.registroocupaciones.presentation.horasextras.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.time.Instant
import java.time.ZoneOffset
import androidx.compose.foundation.verticalScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoraExtraFormScreen(
    viewModel: HoraExtraFormViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var empleadoDropdownExpanded by remember { mutableStateOf(false) }
    var showDateDesdePicker by remember { mutableStateOf(false) }
    var showDateHastaPicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.saved) {
        if (state.saved) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNew) "Nueva Hora Extra" else "Editar Hora Extra") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            val empleadoSeleccionado = state.empleados.find { it.empleadoId == state.empleadoId }
            Box {
                OutlinedTextField(
                    value = empleadoSeleccionado?.nombres ?: "Seleccionar empleado",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Empleado") },
                    trailingIcon = {
                        IconButton(onClick = { empleadoDropdownExpanded = true }) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Abrir empleados"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.empleadoIdError != null,
                    supportingText = state.empleadoIdError?.let { { Text(it) } }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { empleadoDropdownExpanded = true }
                )
                DropdownMenu(
                    expanded = empleadoDropdownExpanded,
                    onDismissRequest = { empleadoDropdownExpanded = false }
                ) {
                    state.empleados.forEach { empleado ->
                        DropdownMenuItem(
                            text = { Text(empleado.nombres) },
                            onClick = {
                                viewModel.onEvent(HoraExtraFormUiEvent.EmpleadoIdChanged(empleado.empleadoId))
                                empleadoDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            val dateDesdePickerState = rememberDatePickerState()
            Box {
                OutlinedTextField(
                    value = state.fechaDesde.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha Desde") },
                    trailingIcon = {
                        IconButton(onClick = { showDateDesdePicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.fechaDesdeError != null,
                    supportingText = state.fechaDesdeError?.let { { Text(it) } }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDateDesdePicker = true })
            }
            if (showDateDesdePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDateDesdePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            dateDesdePickerState.selectedDateMillis?.let { millis ->
                                val localDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC).toLocalDate()
                                viewModel.onEvent(HoraExtraFormUiEvent.FechaDesdeChanged(localDate))
                            }
                            showDateDesdePicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateDesdePicker = false }) { Text("Cancelar") }
                    }
                ) { DatePicker(state = dateDesdePickerState) }
            }

            val dateHastaPickerState = rememberDatePickerState()
            Box {
                OutlinedTextField(
                    value = state.fechaHasta.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha Hasta") },
                    trailingIcon = {
                        IconButton(onClick = { showDateHastaPicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar fecha")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = state.fechaHastaError != null,
                    supportingText = state.fechaHastaError?.let { { Text(it) } }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showDateHastaPicker = true })
            }
            if (showDateHastaPicker) {
                DatePickerDialog(
                    onDismissRequest = { showDateHastaPicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            dateHastaPickerState.selectedDateMillis?.let { millis ->
                                val localDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneOffset.UTC).toLocalDate()
                                viewModel.onEvent(HoraExtraFormUiEvent.FechaHastaChanged(localDate))
                            }
                            showDateHastaPicker = false
                        }) { Text("Aceptar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDateHastaPicker = false }) { Text("Cancelar") }
                    }
                ) { DatePicker(state = dateHastaPickerState) }
            }

            OutlinedTextField(
                value = state.horasTotales,
                onValueChange = { viewModel.onEvent(HoraExtraFormUiEvent.HorasTotalesChanged(it)) },
                label = { Text("Horas Totales de la Semana") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.horasTotalesError != null,
                supportingText = state.horasTotalesError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            OutlinedTextField(
                value = state.horasNocturnas,
                onValueChange = { viewModel.onEvent(HoraExtraFormUiEvent.HorasNocturnasChanged(it)) },
                label = { Text("Horas Nocturnas (9pm–7am)") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.horasNocturnasError != null,
                supportingText = state.horasNocturnasError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            val horasTotalesDouble = state.horasTotales.toDoubleOrNull() ?: 0.0
            val horasNocturnasDouble = state.horasNocturnas.toDoubleOrNull() ?: 0.0
            val sueldo = state.empleados.find { it.empleadoId == state.empleadoId }?.sueldo ?: 0.0

            if (horasTotalesDouble > 44.0 && sueldo > 0.0) {
                val sueldoPorHora = sueldo / 23.53 / 8.0
                val horasExtras   = horasTotalesDouble - 44.0
                val horasAl35     = minOf(horasExtras, 24.0)
                val horasAl100    = maxOf(0.0, horasExtras - 24.0)
                val nocturnas35   = minOf(horasNocturnasDouble, horasAl35)
                val diurnas35     = horasAl35 - nocturnas35
                val monto35  = (diurnas35  * sueldoPorHora * 1.35) +
                        (nocturnas35 * sueldoPorHora * 1.15 * 1.35)
                val monto100 = horasAl100 * sueldoPorHora * 2.0
                val total    = monto35 + monto100

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Resumen de cálculo", style = MaterialTheme.typography.titleSmall)
                        Text("Horas al 35%: ${"%.1f".format(horasAl35)}h  →  RD$${"%.2f".format(monto35)}")
                        Text("Horas al 100%: ${"%.1f".format(horasAl100)}h  →  RD$${"%.2f".format(monto100)}")
                        if (horasNocturnasDouble > 0.0)
                            Text("Nocturnas: ${"%.1f".format(nocturnas35)}h (+15% incluido)")
                        Text(
                            text = "Total a pagar: RD$${"%.2f".format(total)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.onEvent(HoraExtraFormUiEvent.Save) },
                modifier = Modifier.fillMaxWidth(),
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
        }
    }
}