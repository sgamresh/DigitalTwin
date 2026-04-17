package com.example.digitaltwin

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.digitaltwin.di.AppContainer
import com.example.digitaltwin.navigation.AppNavGraph
import com.example.digitaltwin.navigation.Destination
import com.example.digitaltwin.navigation.topLevelDestinations
import com.example.digitaltwin.ui.theme.DigitalTwinTheme

@Composable
fun DigitalTwinApp(
    appContainer: AppContainer,
) {
    DigitalTwinTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val showBottomBar = currentDestination?.route != Destination.Edit.route

        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        tonalElevation = 6.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        topLevelDestinations.forEach { destination ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.route == destination.route
                            } == true

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.label,
                                    )
                                },
                                label = {
                                    Text(destination.label)
                                },
                            )
                        }
                    }
                }
            },
        ) { paddingValues ->
            AppNavGraph(
                navController = navController,
                appContainer = appContainer,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}
