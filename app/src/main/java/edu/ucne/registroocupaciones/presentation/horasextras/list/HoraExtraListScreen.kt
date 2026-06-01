package edu.ucne.registroocupaciones.presentation.horasextras.list

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
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edu.ucne.registroocupaciones.domain.horasextras.model.HoraExtra
import edu.ucne.registroocupaciones.presentation.horasextras.edit.HoraExtraFormScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HoraExtraListScreen(
    viewModel: HoraExtraListViewModel = hiltViewModel(),
    onAddHoraExtra: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    onOpenDrawer: () -> Unit,
    isExpanded: Boolean = false
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
                title = { Text("Registro de Horas Extras") },
                navigationIcon = if (!isExpanded) {
                    {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                        }
                    }
                } else ({})
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
                        onAddHoraExtra()
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar hora extra")
            }
        }
    ) { padding ->
        ListDetailPaneScaffold(
            modifier = Modifier.padding(padding),
            directive = navigator.scaffoldDirective,
            value = navigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    HoraExtraListContent(
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
                    val horaExtraId = navigator.currentDestination?.contentKey ?: 0
                    HoraExtraFormScreen(
                        horaExtraId = horaExtraId,
                        isPanel = true,
                        onBack = { scope.launch { navigator.navigateBack() } }
                    )
                }
            }
        )
    }
}

@Composable
fun HoraExtraListContent(
    state: HoraExtraListUiState,
    onEvent: (HoraExtraListUiEvent) -> Unit,
    onItemClick: (Int) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val totalRegistros = remember(state.horasExtras) { state.horasExtras.size }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(HoraExtraListUiEvent.ClearMessage)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))

        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.horasExtras.isEmpty() -> {
                Text(
                    text = "No hay registros de horas extras",
                    modifier = Modifier.align(Alignment.Center),
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
                        items(state.horasExtras, key = { it.horaExtraId }) { horaExtra ->
                            val sueldo = state.empleados
                                .find { it.empleadoId == horaExtra.empleadoId }?.sueldo ?: 0.0
                            val nombreEmpleado = state.empleados
                                .find { it.empleadoId == horaExtra.empleadoId }?.nombres ?: "Empleado desconocido"

                            HoraExtraItem(
                                horaExtra = horaExtra,
                                sueldo = sueldo,
                                nombreEmpleado = nombreEmpleado,
                                onClick = { onItemClick(horaExtra.horaExtraId) }
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
                                Text("Conteo:", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    "$totalRegistros",
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
fun HoraExtraItem(
    horaExtra: HoraExtra,
    sueldo: Double,
    nombreEmpleado: String,
    onClick: () -> Unit
) {
    val horasExtras = maxOf(0.0, horaExtra.horasTotales - 44.0)
    val horasAl35 = minOf(horasExtras, 24.0)
    val horasAl100 = maxOf(0.0, horasExtras - 24.0)
    val sueldoPorHora = sueldo / 23.53 / 8.0
    val nocturnas35 = minOf(horaExtra.horasNocturnas, horasAl35)
    val diurnas35 = horasAl35 - nocturnas35
    val monto35 = (diurnas35 * sueldoPorHora * 1.35) + (nocturnas35 * sueldoPorHora * 1.15 * 1.35)
    val monto100 = horasAl100 * sueldoPorHora * 2.0
    val total = monto35 + monto100

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nombreEmpleado, style = MaterialTheme.typography.bodyLarge)
                Text("Período: ${horaExtra.fechaDesde} → ${horaExtra.fechaHasta}",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Horas totales: ${horaExtra.horasTotales}h  |  Extras: ${horasExtras}h",
                    style = MaterialTheme.typography.bodyMedium)
                Text("Al 35%: ${horasAl35}h  |  Al 100%: ${horasAl100}h",
                    style = MaterialTheme.typography.bodyMedium)
                if (horaExtra.horasNocturnas > 0.0)
                    Text("Nocturnas: ${horaExtra.horasNocturnas}h (+15%)",
                        style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Total a pagar: RD${"%.2f".format(total)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}