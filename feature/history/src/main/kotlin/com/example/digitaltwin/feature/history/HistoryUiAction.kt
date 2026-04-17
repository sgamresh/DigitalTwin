package com.example.digitaltwin.feature.history

sealed interface HistoryUiAction {
    data class DeleteActivity(val activityId: Long) : HistoryUiAction
    data object MessageShown : HistoryUiAction
}

