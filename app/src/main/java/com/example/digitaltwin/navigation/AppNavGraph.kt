package com.example.digitaltwin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.digitaltwin.di.AppContainer
import com.example.digitaltwin.feature.edit.EditActivityRoute
import com.example.digitaltwin.feature.history.HistoryRoute
import com.example.digitaltwin.feature.tracker.TrackerRoute

@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Destination.Timer.route,
        modifier = modifier,
    ) {
        composable(route = Destination.Timer.route) {
            TrackerRoute(
                addActivityUseCase = appContainer.addActivityUseCase,
                onOpenHistory = {
                    navController.navigate(Destination.History.route)
                },
            )
        }

        composable(route = Destination.History.route) {
            HistoryRoute(
                getGroupedHistoryUseCase = appContainer.getGroupedHistoryUseCase,
                deleteActivityUseCase = appContainer.deleteActivityUseCase,
                onEditActivity = { activityId ->
                    navController.navigate(Destination.Edit.createRoute(activityId))
                },
                onOpenTimer = {
                    navController.navigate(Destination.Timer.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        composable(
            route = Destination.Edit.route,
            arguments = listOf(
                navArgument(Destination.Edit.ARG_ACTIVITY_ID) {
                    type = NavType.LongType
                },
            ),
        ) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getLong(Destination.Edit.ARG_ACTIVITY_ID) ?: 0L
            EditActivityRoute(
                activityId = activityId,
                getActivityUseCase = appContainer.getActivityUseCase,
                updateActivityUseCase = appContainer.updateActivityUseCase,
                deleteActivityUseCase = appContainer.deleteActivityUseCase,
                onDone = {
                    navController.popBackStack()
                },
            )
        }
    }
}
