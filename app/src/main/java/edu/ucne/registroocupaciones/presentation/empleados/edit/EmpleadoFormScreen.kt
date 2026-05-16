package edu.ucne.registroocupaciones.presentation.empleados.edit

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoFormScreen(
    viewModel: EmpleadoFormViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isNew) "Nuevo Empleado" else "Editar Empleado") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.nombres,
                onValueChange = { viewModel.onEvent(EmpleadoFormUiEvent.NombresChanged(it)) },
                label = { Text("Nombres") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_nombres"),
                isError = state.nombresError != null,
                supportingText = state.nombresError?.let { { Text(it) } },
                singleLine = true
            )

            OutlinedTextField(
                value = state.sexo,
                onValueChange = { viewModel.onEvent(EmpleadoFormUiEvent.SexoChanged(it)) },
                label = { Text("Sexo (M / F)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sexo"),
                singleLine = true
            )

            OutlinedTextField(
                value = state.fechaIngreso,
                onValueChange = { viewModel.onEvent(EmpleadoFormUiEvent.FechaIngresoChanged(it)) },
                label = { Text("Fecha de Ingreso (yyyy-MM-dd)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_fecha_ingreso"),
                isError = state.fechaIngresoError != null,
                supportingText = state.fechaIngresoError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            OutlinedTextField(
                value = state.sueldo,
                onValueChange = { viewModel.onEvent(EmpleadoFormUiEvent.SueldoChanged(it)) },
                label = { Text("Sueldo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_sueldo"),
                isError = state.sueldoError != null,
                supportingText = state.sueldoError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Button(
                onClick = { viewModel.onEvent(EmpleadoFormUiEvent.Save) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_save"),
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