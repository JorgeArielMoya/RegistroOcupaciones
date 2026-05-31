package edu.ucne.registroocupaciones.presentation.empleados.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.empleados.model.Empleado
import edu.ucne.registroocupaciones.presentation.empleados.edit.EmpleadoFormScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoListScreen(
    viewModel: EmpleadoListViewModel = hiltViewModel(),
    onAddEmpleado: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val navigator = rememberListDetailPaneScaffoldNavigator<Int>()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registro de Empleados") },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] ==
                        PaneAdaptedValue.Expanded
                    ) {
                        scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, 0) }
                    } else {
                        onAddEmpleado()
                    }
                },
                modifier = Modifier.testTag("fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar empleado")
            }
        }
    ) { padding ->
        ListDetailPaneScaffold(
            modifier = Modifier.padding(padding),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    EmpleadoListContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onItemClick = { id ->
                            if (navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] ==
                                PaneAdaptedValue.Expanded
                            ) {
                                scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, id) }
                            } else {
                                onNavigateToEdit(id)
                            }
                        }
                    )
                }
            },
            detailPane = {
                AnimatedPane {
                    EmpleadoFormScreen(
                        isPanel = true,
                        onBack = { scope.launch { navigator.navigateBack() } }
                    )
                }
            }
        )
    }
}

@Composable
fun EmpleadoListContent(
    state: EmpleadoListUiState,
    onEvent: (EmpleadoListUiEvent) -> Unit,
    onItemClick: (Int) -> Unit
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

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).testTag("loading")
                )
            }
            state.empleados.isEmpty() -> {
                Text(
                    text = "No hay empleados",
                    modifier = Modifier.align(Alignment.Center).testTag("empty_message"),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.empleados, key = { it.empleadoId }) { empleado ->
                            EmpleadoItem(
                                empleado = empleado,
                                onDelete = { onEvent(EmpleadoListUiEvent.Delete(empleado.empleadoId)) },
                                onClick = { onItemClick(empleado.empleadoId) }
                            )
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.padding(end = 32.dp)) {
                                Text("Conteo", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "$totalEmpleados",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Total:", style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    String.format("$%,.2f", sumatoriaSueldos),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoItem(
    empleado: Empleado,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().testTag("empleado_item_${empleado.empleadoId}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: ${empleado.empleadoId}", style = MaterialTheme.typography.bodyLarge)
                Text("Nombre: ${empleado.nombres}", style = MaterialTheme.typography.bodyLarge)
                Text("Sexo: ${empleado.sexo}", style = MaterialTheme.typography.bodyMedium)
                Text("Fecha Ingreso: ${empleado.fechaIngreso}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Sueldo: ${empleado.sueldo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("btn_delete_${empleado.empleadoId}")
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar empleado")
            }
        }
    }
}