package edu.ucne.registroocupaciones.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.ucne.registroocupaciones.presentation.ocupaciones.edit.OcupacionFormScreen
import edu.ucne.registroocupaciones.presentation.ocupaciones.list.OcupacionListScreen

@Composable
fun OcupacionNavHost(
    navController: NavHostController = rememberNavController()
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
                }
            )
        }

        composable<Screen.OcupacionForm> {
            OcupacionFormScreen(
                onBack = {
                    navController.navigate(Screen.OcupacionList) {
                        popUpTo(Screen.OcupacionList) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}