package com.example.digitaltwin.feature.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.digitaltwin.core.common.DateTimeFormatters
import com.example.digitaltwin.core.model.ActivityRecord

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onAction: (HistoryUiAction) -> Unit,
    onEditActivity: (Long) -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (uiState.historyGroups.isEmpty()) {
        EmptyHistoryState(
            statusMessage = uiState.statusMessage,
            onOpenTimer = onOpenTimer,
            modifier = modifier,
        )
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Activity History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Review past sessions grouped by day, then edit or delete anything that needs cleanup.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        if (uiState.statusMessage != null) {
            item {
                HistoryStatusBanner(
                    message = uiState.statusMessage,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        uiState.historyGroups.forEach { group ->
            stickyHeader(key = group.date.toString()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = DateTimeFormatters.formatDate(group.date),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "${group.activities.size} activities",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = CircleShape,
                        ) {
                            Text(
                                text = DateTimeFormatters.formatDuration(
                                    group.activities.sumOf { it.durationMillis },
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            )
                        }
                    }
                }
            }

            items(
                items = group.activities,
                key = { activity -> activity.id },
            ) { activity ->
                HistoryCard(
                    activity = activity,
                    isDeleting = uiState.deletingActivityIds.contains(activity.id),
                    onEditActivity = onEditActivity,
                    onDeleteActivity = { id ->
                        onAction(HistoryUiAction.DeleteActivity(id))
                    },
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(
    statusMessage: String?,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 2.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape,
                ) {
                    Text(
                        text = "00",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    )
                }
                Text(
                    text = "No saved activities yet",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Track a session, stop the timer, and add it to history to start building your daily record.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (statusMessage != null) {
                    HistoryStatusBanner(
                        message = statusMessage,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Button(
                    onClick = onOpenTimer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Open Timer")
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(
    activity: ActivityRecord,
    isDeleting: Boolean,
    onEditActivity: (Long) -> Unit,
    onDeleteActivity: (Long) -> Unit,
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = DateTimeFormatters.formatTimeRange(
                            startTimeMillis = activity.startTime,
                            endTimeMillis = activity.endTime,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = CircleShape,
                ) {
                    Text(
                        text = DateTimeFormatters.formatDuration(activity.durationMillis),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = { onEditActivity(activity.id) },
                    modifier = Modifier.weight(1f),
                    enabled = !isDeleting,
                ) {
                    Text("Edit")
                }
                OutlinedButton(
                    onClick = { onDeleteActivity(activity.id) },
                    modifier = Modifier.weight(1f),
                    enabled = !isDeleting,
                ) {
                    Text(if (isDeleting) "Deleting..." else "Delete")
                }
            }
        }
    }
}

@Composable
private fun HistoryStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.large,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        )
    }
}
