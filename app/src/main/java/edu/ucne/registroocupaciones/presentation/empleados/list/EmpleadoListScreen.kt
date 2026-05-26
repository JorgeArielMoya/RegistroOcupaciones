package edu.ucne.registroocupaciones.presentation.empleados.list

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado

@Composable
fun EmpleadoListScreen(
    viewModel: EmpleadoListViewModel = hiltViewModel(),
    onAddEmpleado: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToCreate) {
        if (state.navigateToCreate) {
            onAddEmpleado()
        }
    }

    LaunchedEffect(state.navigateToEditId) {
        state.navigateToEditId?.let { id ->
            onNavigateToEdit(id)
        }
    }

    EmpleadoListBody(state, viewModel::onEvent, onAddEmpleado, onOpenDrawer)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoListBody(
    state: EmpleadoListUiState,
    onEvent: (EmpleadoListUiEvent) -> Unit,
    onAddEmpleado: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val totalEmpleados = remember(state.empleados) { state.empleados.size }
    val sumatoriaSueldos = remember(state.empleados) { state.empleados.sumOf { it.sueldo ?: 0.0 } }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(EmpleadoListUiEvent.ClearMessage)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro de Empleados") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Abrir menú"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddEmpleado,
                modifier = Modifier.testTag("fab_add")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar empleado"
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )
            } else {
                if (state.empleados.isEmpty()) {
                    Text(
                        text = "No hay empleados",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("empty_message"),
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
                                items = state.empleados,
                                key = { it.empleadoId }
                            ) { empleado ->
                                EmpleadoItem(
                                    empleado = empleado,
                                    onDelete = {
                                        onEvent(EmpleadoListUiEvent.Delete(empleado.empleadoId))
                                    },
                                    onClick = {
                                        onEvent(EmpleadoListUiEvent.Edit(empleado.empleadoId))
                                    }
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
                                        text = "Conteo",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$totalEmpleados",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Total:",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = String.format("$%,.2f", sumatoriaSueldos),
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
fun EmpleadoItem(
    empleado: Empleado,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("empleado_item_${empleado.empleadoId}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "ID: ${empleado.empleadoId}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Nombre: ${empleado.nombres}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Sexo: ${empleado.sexo}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Fecha Ingreso: ${empleado.fechaIngreso}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Sueldo: ${empleado.sueldo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("btn_delete_${empleado.empleadoId}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar empleado"
                )
            }
        }
    }
}