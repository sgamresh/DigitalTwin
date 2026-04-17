package com.example.digitaltwin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Timer : Destination(
        route = "timer",
        label = "Timer",
        icon = Icons.Outlined.Timer,
    )

    data object History : Destination(
        route = "history",
        label = "History",
        icon = Icons.Outlined.History,
    )

    data object Edit : Destination(
        route = "edit/{activityId}",
        label = "Edit",
        icon = Icons.Outlined.Edit,
    ) {
        const val ARG_ACTIVITY_ID = "activityId"

        fun createRoute(activityId: Long): String = "edit/$activityId"
    }
}

val topLevelDestinations = listOf(
    Destination.Timer,
    Destination.History,
)

