package com.example.aforoestadio.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aforoestadio.presentation.ui.LogScreen
import com.example.aforoestadio.presentation.ui.MapScreen
import com.example.aforoestadio.presentation.ui.MetricsScreen
import com.example.aforoestadio.presentation.viewmodel.StadiumViewModel

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Map : Screen("map", "Mapa", Icons.Default.Map)
    data object Metrics : Screen("metrics", "Métricas", Icons.Default.BarChart)
    data object Log : Screen("log", "Log", Icons.Default.List)
}

@Composable
fun MainScreen(
    viewModel: StadiumViewModel = viewModel()
) {
    val navController = rememberNavController()
    val stadiumState by viewModel.stadiumState.collectAsStateWithLifecycle()
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val screens = listOf(Screen.Map, Screen.Metrics, Screen.Log)

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            snackbarHostState.showSnackbar(
                message = "Asistente asignado - ${event.eventId}\n" +
                        "${event.assignedSector?.name} - Bloque ${event.assignedBlock?.name} " +
                        "(${event.distance}m)",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                screens.forEach { screen ->
                    val selected = currentRoute == screen.route

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(text = screen.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Map.route) {
                MapScreen(
                    state = stadiumState,
                    connectionState = connectionState
                )
            }
            composable(Screen.Metrics.route) {
                MetricsScreen(state = stadiumState)
            }
            composable(Screen.Log.route) {
                LogScreen(state = stadiumState)
            }
        }
    }
}