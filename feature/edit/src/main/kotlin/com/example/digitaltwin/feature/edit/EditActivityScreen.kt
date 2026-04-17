package com.example.digitaltwin.feature.edit

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.digitaltwin.core.common.DateTimeFormatters
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun EditActivityScreen(
    uiState: EditActivityUiState,
    onAction: (EditActivityUiAction) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val zoneId = remember { ZoneId.systemDefault() }
    val startDateTime = remember(uiState.startTimeMillis) {
        Instant.ofEpochMilli(uiState.startTimeMillis.coerceAtLeast(0L)).atZone(zoneId).toLocalDateTime()
    }
    val endDateTime = remember(uiState.endTimeMillis) {
        Instant.ofEpochMilli(uiState.endTimeMillis.coerceAtLeast(0L)).atZone(zoneId).toLocalDateTime()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.18f))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 560.dp),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 3.dp,
            shadowElevation = 16.dp,
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = "Edit Activity",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = DateTimeFormatters.formatDuration(
                                    durationMillis = uiState.endTimeMillis - uiState.startTimeMillis,
                                ),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        TextButton(
                            onClick = onDismiss,
                            enabled = !uiState.isSaving && !uiState.isDeleting,
                        ) {
                            Text("Close")
                        }
                    }

                    Text(
                        text = "Adjust the label and recorded time range, then save the update when everything looks right.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    if (uiState.statusMessage != null) {
                        EditStatusBanner(
                            message = uiState.statusMessage,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    TextField(
                        value = uiState.name,
                        onValueChange = { onAction(EditActivityUiAction.NameChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        label = { Text("Activity name") },
                        singleLine = true,
                        isError = uiState.nameError != null,
                        supportingText = {
                            if (uiState.nameError != null) {
                                Text(uiState.nameError)
                            }
                        },
                        shape = MaterialTheme.shapes.large,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface,
                        ),
                    )

                    DateTimeEditorCard(
                        title = "Start",
                        dateLabel = DateTimeFormatters.formatDate(uiState.startTimeMillis),
                        timeLabel = DateTimeFormatters.formatTime(uiState.startTimeMillis),
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        selectedDate = startDateTime.toLocalDate(),
                        selectedTime = startDateTime.toLocalTime().withSecond(0).withNano(0),
                        onDatePicked = { onAction(EditActivityUiAction.StartDateChanged(it)) },
                        onTimePicked = { onAction(EditActivityUiAction.StartTimeChanged(it)) },
                    )

                    DateTimeEditorCard(
                        title = "End",
                        dateLabel = DateTimeFormatters.formatDate(uiState.endTimeMillis),
                        timeLabel = DateTimeFormatters.formatTime(uiState.endTimeMillis),
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        selectedDate = endDateTime.toLocalDate(),
                        selectedTime = endDateTime.toLocalTime().withSecond(0).withNano(0),
                        onDatePicked = { onAction(EditActivityUiAction.EndDateChanged(it)) },
                        onTimePicked = { onAction(EditActivityUiAction.EndTimeChanged(it)) },
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TextButton(
                            onClick = { onAction(EditActivityUiAction.DeleteActivity) },
                            enabled = uiState.canDelete,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text(if (uiState.isDeleting) "Deleting..." else "Delete")
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        OutlinedButton(
                            onClick = onDismiss,
                            enabled = !uiState.isSaving && !uiState.isDeleting,
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = { onAction(EditActivityUiAction.SaveChanges) },
                            enabled = uiState.canSave,
                        ) {
                            Text(if (uiState.isSaving) "Saving..." else "Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateTimeEditorCard(
    title: String,
    dateLabel: String,
    timeLabel: String,
    enabled: Boolean,
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDatePicked: (LocalDate) -> Unit,
    onTimePicked: (LocalTime) -> Unit,
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                FilledTonalButton(
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                onDatePicked(LocalDate.of(year, month + 1, dayOfMonth))
                            },
                            selectedDate.year,
                            selectedDate.monthValue - 1,
                            selectedDate.dayOfMonth,
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                ) {
                    Text("Change Date")
                }
                OutlinedButton(
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                onTimePicked(LocalTime.of(hourOfDay, minute))
                            },
                            selectedTime.hour,
                            selectedTime.minute,
                            false,
                        ).show()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                ) {
                    Text("Change Time")
                }
            }
        }
    }
}

@Composable
private fun EditStatusBanner(
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
