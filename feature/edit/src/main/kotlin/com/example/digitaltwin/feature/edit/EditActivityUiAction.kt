package com.example.digitaltwin.feature.edit

import java.time.LocalDate
import java.time.LocalTime

sealed interface EditActivityUiAction {
    data class NameChanged(val value: String) : EditActivityUiAction
    data class StartDateChanged(val value: LocalDate) : EditActivityUiAction
    data class StartTimeChanged(val value: LocalTime) : EditActivityUiAction
    data class EndDateChanged(val value: LocalDate) : EditActivityUiAction
    data class EndTimeChanged(val value: LocalTime) : EditActivityUiAction
    data object SaveChanges : EditActivityUiAction
    data object DeleteActivity : EditActivityUiAction
    data object MessageShown : EditActivityUiAction
}

