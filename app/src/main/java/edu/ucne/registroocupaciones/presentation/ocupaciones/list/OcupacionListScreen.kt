package edu.ucne.registroocupaciones.presentation.ocupaciones.list

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.ocupaciones.model.Ocupacion
import edu.ucne.registroocupaciones.presentation.ocupaciones.edit.OcupacionFormScreen

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OcupacionListScreen(
    viewModel: OcupacionListViewModel = hiltViewModel(),
    onAddOcupacion: () -> Unit,
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
                title = { Text("Registro de Ocupaciones") },
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
                        androidx.compose.material3.adaptive.layout.PaneAdaptedValue.Expanded) {
                        scope.launch { navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, 0) }
                    } else {
                        onAddOcupacion()
                    }
                },
                modifier = Modifier.testTag("fab_add")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar ocupación")
            }
        }
    ) { padding ->
        ListDetailPaneScaffold(
            modifier = Modifier.padding(padding),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,

            listPane = {
                AnimatedPane {
                    OcupacionListContent(
                        state = state,
                        onEvent = viewModel::onEvent,
                        onItemClick = { id ->
                            if (navigator.scaffoldValue[ListDetailPaneScaffoldRole.Detail] ==
                                androidx.compose.material3.adaptive.layout.PaneAdaptedValue.Expanded) {
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
                    OcupacionFormScreen(
                        isPanel = true,
                        onBack = { scope.launch { navigator.navigateBack() } }
                    )
                }
            }
        )
    }
}

@Composable
fun OcupacionListContent(
    state: OcupacionListUiState,
    onEvent: (OcupacionListUiEvent) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val totalOcupaciones = remember(state.ocupaciones) { state.ocupaciones.size }
    val sumatoriaSueldos = remember(state.ocupaciones) { state.ocupaciones.sumOf { it.sueldo ?: 0.0 } }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(OcupacionListUiEvent.ClearMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading")
                )
            }
            state.ocupaciones.isEmpty() -> {
                Text(
                    text = "No hay ocupaciones",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("empty_message"),
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
                        items(state.ocupaciones, key = { it.ocupacionId }) { ocupacion ->
                            OcupacionItem(
                                ocupacion = ocupacion,
                                onClick = { onItemClick(ocupacion.ocupacionId) }
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
                                Text("Conteo:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "$totalOcupaciones",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column {
                                Text("Total:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    String.format("$%,.2f", sumatoriaSueldos),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
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
fun OcupacionItem(
    ocupacion: Ocupacion,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ocupacion_item_${ocupacion.ocupacionId}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("ID: ${ocupacion.ocupacionId}", style = MaterialTheme.typography.bodyLarge)
                Text("Descripcion: ${ocupacion.descripcion}", style = MaterialTheme.typography.bodyLarge)
                Text(
                    "Sueldo: ${ocupacion.sueldo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}