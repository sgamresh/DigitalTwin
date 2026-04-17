package com.example.digitaltwin.feature.edit

data class EditActivityUiState(
    val activityId: Long = 0L,
    val isLoading: Boolean = true,
    val name: String = "",
    val startTimeMillis: Long = 0L,
    val endTimeMillis: Long = 0L,
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val statusMessage: String? = null,
) {
    val canSave: Boolean
        get() = !isLoading && !isSaving && !isDeleting

    val canDelete: Boolean
        get() = activityId > 0L && !isLoading && !isSaving && !isDeleting
}
