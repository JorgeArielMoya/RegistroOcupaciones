package edu.ucne.registroocupaciones.presentation.horasextras.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra

@Composable
fun HoraExtraListScreen(
    viewModel: HoraExtraListViewModel = hiltViewModel(),
    onAddHoraExtra: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) onAddHoraExtra()
    }

    LaunchedEffect(state.navigateToEditId) {
        state.navigateToEditId?.let { id -> onNavigateToEdit(id) }
    }

    HoraExtraListBody(state, viewModel::onEvent, onAddHoraExtra, onOpenDrawer)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoraExtraListBody(
    state: HoraExtraListUiState,
    onEvent: (HoraExtraListUiEvent) -> Unit,
    onAddHoraExtra: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val totalRegistros = remember(state.horasExtras) { state.horasExtras.size }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(HoraExtraListUiEvent.ClearMessage)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro de Horas Extras") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHoraExtra) {
                Icon(Icons.Default.Add, contentDescription = "Agregar hora extra")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (state.horasExtras.isEmpty()) {
                    Text(
                        text = "No hay registros de horas extras",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.horasExtras,
                                key = { it.horaExtraId }
                            ) { horaExtra ->
                                val sueldo = state.empleados
                                    .find { it.empleadoId == horaExtra.empleadoId }
                                    ?.sueldo ?: 0.0

                                val nombreEmpleado = state.empleados
                                    .find { it.empleadoId == horaExtra.empleadoId }
                                    ?.nombres ?: "Empleado desconocido"

                                HoraExtraItem(
                                    horaExtra = horaExtra,
                                    sueldo = sueldo,
                                    nombreEmpleado = nombreEmpleado,
                                    onDelete = { onEvent(HoraExtraListUiEvent.Delete(horaExtra.horaExtraId)) },
                                    onClick = { onEvent(HoraExtraListUiEvent.Edit(horaExtra.horaExtraId)) }
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(end = 32.dp)) {
                                    Text(
                                        text = "Conteo:",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "$totalRegistros",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoraExtraItem(
    horaExtra: HoraExtra,
    sueldo: Double,
    nombreEmpleado: String,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val horasExtras   = maxOf(0.0, horaExtra.horasTotales - 44.0)
    val horasAl35     = minOf(horasExtras, 24.0)
    val horasAl100    = maxOf(0.0, horasExtras - 24.0)

    val sueldoPorHora = sueldo / 23.53 / 8.0

    val nocturnas35   = minOf(horaExtra.horasNocturnas, horasAl35)
    val diurnas35     = horasAl35 - nocturnas35

    val monto35  = (diurnas35  * sueldoPorHora * 1.35) +
            (nocturnas35 * sueldoPorHora * 1.15 * 1.35)
    val monto100 = horasAl100  * sueldoPorHora * 2.0
    val total    = monto35 + monto100

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombreEmpleado,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Período: ${horaExtra.fechaDesde} → ${horaExtra.fechaHasta}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Horas totales: ${horaExtra.horasTotales}h  |  Extras: ${horasExtras}h",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Al 35%: ${horasAl35}h  |  Al 100%: ${horasAl100}h",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (horaExtra.horasNocturnas > 0.0) {
                    Text(
                        text = "Nocturnas: ${horaExtra.horasNocturnas}h (+15%)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Text(
                    text = "Total a pagar: RD${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}