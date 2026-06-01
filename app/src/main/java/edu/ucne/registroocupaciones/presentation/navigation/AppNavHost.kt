package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ucne.registroocupaciones.presentation.empleados.edit.EmpleadoFormScreen
import edu.ucne.registroocupaciones.presentation.empleados.list.EmpleadoListScreen
import edu.ucne.registroocupaciones.presentation.horasextras.edit.HoraExtraFormScreen
import edu.ucne.registroocupaciones.presentation.horasextras.list.HoraExtraListScreen
import edu.ucne.registroocupaciones.presentation.ocupaciones.edit.OcupacionFormScreen
import edu.ucne.registroocupaciones.presentation.ocupaciones.list.OcupacionListScreen
import kotlinx.coroutines.launch
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.activity.compose.LocalActivity

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val activity = LocalActivity.current!!
    val windowSizeClass = calculateWindowSizeClass(activity)
    val isExpanded = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    DrawerMenu(
        drawerState = drawerState,
        navHostController = navController,
        isExpanded = isExpanded
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.OcupacionList
        ) {

            composable<Screen.OcupacionList> {
                OcupacionListScreen(
                    onAddOcupacion = {
                        navController.navigate(Screen.OcupacionForm(0))
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.OcupacionForm(id))
                    },
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            composable<Screen.OcupacionForm> {
                OcupacionFormScreen(
                    isPanel = false,
                    onBack = {
                        navController.navigate(Screen.OcupacionList) {
                            popUpTo(Screen.OcupacionList) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.EmpleadoList> {
                EmpleadoListScreen(
                    onAddEmpleado = {
                        navController.navigate(Screen.EmpleadoForm(0))
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.EmpleadoForm(id))
                    },
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            composable<Screen.EmpleadoForm> {
                EmpleadoFormScreen(
                    isPanel = false,
                    onBack = {
                        navController.navigate(Screen.EmpleadoList) {
                            popUpTo(Screen.EmpleadoList) { inclusive = true }
                        }
                    }
                )
            }

            composable<Screen.HoraExtraList> {
                HoraExtraListScreen(
                    onAddHoraExtra = {
                        navController.navigate(Screen.HoraExtraForm(0))
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.HoraExtraForm(id))
                    },
                    onOpenDrawer = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            composable<Screen.HoraExtraForm> {
                HoraExtraFormScreen(
                    isPanel = false,
                    onBack = {
                        navController.navigate(Screen.HoraExtraList) {
                            popUpTo(Screen.HoraExtraList) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}