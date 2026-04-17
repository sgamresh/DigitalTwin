package com.example.digitaltwin.feature.tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.digitaltwin.core.common.DateTimeFormatters

@Composable
fun TrackerScreen(
    uiState: TrackerUiState,
    onAction: (TrackerUiAction) -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Digital Twin",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Track focused work with a timer that stays accurate in the background, then save the session when you are done.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = "CURRENT TIMER",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    )
                }
                Text(
                    text = DateTimeFormatters.formatTimerDuration(uiState.elapsedMillis),
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = when {
                        uiState.isRunning -> "Timer running. Stop when this session is complete."
                        uiState.isSaving -> "Saving this activity to your history."
                        uiState.canSave -> "Session captured. Add it to history when you are ready."
                        else -> "Ready to start your next focused block."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 1.dp,
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "Activity details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Give this session a clear name so it is easy to find later in history.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                TextField(
                    value = uiState.activityName,
                    onValueChange = { onAction(TrackerUiAction.ActivityNameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving,
                    label = { Text("Activity name") },
                    placeholder = { Text("Deep work session") },
                    singleLine = true,
                    isError = uiState.activityNameError != null,
                    supportingText = {
                        if (uiState.activityNameError != null) {
                            Text(uiState.activityNameError)
                        }
                    },
                    shape = MaterialTheme.shapes.large,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
            }
        }

        if (uiState.statusMessage != null) {
            TrackerStatusBanner(
                message = uiState.statusMessage,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        when {
            uiState.canStart -> {
                Button(
                    onClick = { onAction(TrackerUiAction.StartTimer) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.canStart,
                ) {
                    Text("Start Timer")
                }
            }

            uiState.canStop -> {
                FilledTonalButton(
                    onClick = { onAction(TrackerUiAction.StopTimer) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.canStop,
                ) {
                    Text("Stop Timer")
                }
            }
        }

        if (uiState.canSave) {
            Button(
                onClick = { onAction(TrackerUiAction.SaveActivity) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.canAddActivity,
            ) {
                Text(if (uiState.isSaving) "Saving..." else "+ Add Activity")
            }
        }

        OutlinedButton(
            onClick = onOpenHistory,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
        ) {
            Text("View History")
        }
    }
}

@Composable
private fun TrackerStatusBanner(
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
