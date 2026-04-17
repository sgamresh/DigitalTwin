package com.example.digitaltwin.feature.tracker

data class TrackerUiState(
    val activityName: String = "",
    val isRunning: Boolean = false,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val elapsedMillis: Long = 0L,
    val activityNameError: String? = null,
    val isSaving: Boolean = false,
    val statusMessage: String? = null,
) {
    val canStart: Boolean
        get() = !isSaving && !isRunning && startTime == null

    val canStop: Boolean
        get() = !isSaving && isRunning

    val canSave: Boolean
        get() = !isRunning && startTime != null && endTime != null

    val hasValidActivityName: Boolean
        get() = activityName.trim().isNotEmpty()

    val canAddActivity: Boolean
        get() = canSave && !isSaving && hasValidActivityName
}
