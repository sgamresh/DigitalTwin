package com.example.digitaltwin.feature.tracker

sealed interface TrackerUiAction {
    data class ActivityNameChanged(val value: String) : TrackerUiAction
    data object StartTimer : TrackerUiAction
    data object StopTimer : TrackerUiAction
    data object SaveActivity : TrackerUiAction
    data object MessageShown : TrackerUiAction
}

