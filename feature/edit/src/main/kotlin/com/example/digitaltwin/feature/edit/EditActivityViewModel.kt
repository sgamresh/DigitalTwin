package com.example.digitaltwin.feature.edit

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.digitaltwin.core.model.ActivityRecord
import com.example.digitaltwin.domain.activity.usecase.DeleteActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.UpdateActivityUseCase
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditActivityViewModel(
    private val activityId: Long,
    private val getActivityUseCase: GetActivityUseCase,
    private val updateActivityUseCase: UpdateActivityUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditActivityUiState())
    val uiState: StateFlow<EditActivityUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<EditActivityEffect>()
    val effects: SharedFlow<EditActivityEffect> = _effects.asSharedFlow()

    init {
        loadActivity()
    }

    fun onAction(action: EditActivityUiAction) {
        when (action) {
            is EditActivityUiAction.NameChanged -> {
                _uiState.update {
                    it.copy(
                        name = action.value,
                        nameError = null,
                    )
                }
            }

            is EditActivityUiAction.StartDateChanged -> {
                _uiState.update { state ->
                    state.copy(startTimeMillis = replaceDate(state.startTimeMillis, action.value))
                }
            }

            is EditActivityUiAction.StartTimeChanged -> {
                _uiState.update { state ->
                    state.copy(startTimeMillis = replaceTime(state.startTimeMillis, action.value))
                }
            }

            is EditActivityUiAction.EndDateChanged -> {
                _uiState.update { state ->
                    state.copy(endTimeMillis = replaceDate(state.endTimeMillis, action.value))
                }
            }

            is EditActivityUiAction.EndTimeChanged -> {
                _uiState.update { state ->
                    state.copy(endTimeMillis = replaceTime(state.endTimeMillis, action.value))
                }
            }

            EditActivityUiAction.SaveChanges -> saveChanges()
            EditActivityUiAction.DeleteActivity -> deleteActivity()
            EditActivityUiAction.MessageShown -> _uiState.update { it.copy(statusMessage = null) }
        }
    }

    private fun loadActivity() {
        viewModelScope.launch {
            try {
                val activity = getActivityUseCase(activityId)
                if (activity == null) {
                    _uiState.value = EditActivityUiState(
                        activityId = activityId,
                        isLoading = false,
                        statusMessage = "Activity not found.",
                    )
                    return@launch
                }

                _uiState.value = EditActivityUiState(
                    activityId = activity.id,
                    isLoading = false,
                    name = activity.name,
                    startTimeMillis = activity.startTime,
                    endTimeMillis = activity.endTime,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: SQLiteException) {
                _uiState.value = EditActivityUiState(
                    activityId = activityId,
                    isLoading = false,
                    statusMessage = "The activity could not be loaded right now.",
                )
            } catch (exception: Exception) {
                _uiState.value = EditActivityUiState(
                    activityId = activityId,
                    isLoading = false,
                    statusMessage = "Something went wrong while loading the activity.",
                )
            }
        }
    }

    private fun saveChanges() {
        val currentState = _uiState.value
        if (!currentState.canSave) return

        val trimmedName = currentState.name.trim()
        if (trimmedName.isBlank()) {
            _uiState.update {
                it.copy(
                    nameError = "Activity name can't be empty.",
                    statusMessage = "Enter an activity name before saving.",
                )
            }
            return
        }

        if (currentState.endTimeMillis < currentState.startTimeMillis) {
            _uiState.update {
                it.copy(statusMessage = "End time must be after the start time.")
            }
            return
        }

        _uiState.update {
            it.copy(
                isSaving = true,
                nameError = null,
                statusMessage = null,
            )
        }

        viewModelScope.launch {
            try {
                updateActivityUseCase(
                    ActivityRecord(
                        id = currentState.activityId,
                        name = trimmedName,
                        startTime = currentState.startTimeMillis,
                        endTime = currentState.endTimeMillis,
                    ),
                )
                _effects.emit(EditActivityEffect.NavigateBack)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        nameError = if (trimmedName.isBlank()) "Activity name can't be empty." else null,
                        statusMessage = exception.message ?: "Check the activity details and try again.",
                    )
                }
            } catch (exception: IllegalStateException) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        statusMessage = exception.message ?: "The activity could not be updated.",
                    )
                }
            } catch (exception: SQLiteException) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        statusMessage = "The activity could not be updated right now. Please try again.",
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        statusMessage = "Something went wrong while saving the activity.",
                    )
                }
            }
        }
    }

    private fun deleteActivity() {
        val currentState = _uiState.value
        if (!currentState.canDelete) return

        _uiState.update {
            it.copy(
                isDeleting = true,
                statusMessage = null,
            )
        }

        viewModelScope.launch {
            try {
                deleteActivityUseCase(currentState.activityId)
                _effects.emit(EditActivityEffect.NavigateBack)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        statusMessage = exception.message ?: "The activity could not be deleted.",
                    )
                }
            } catch (exception: IllegalStateException) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        statusMessage = exception.message ?: "The activity could not be deleted.",
                    )
                }
            } catch (exception: SQLiteException) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        statusMessage = "The activity could not be deleted right now. Please try again.",
                    )
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        statusMessage = "Something went wrong while deleting the activity.",
                    )
                }
            }
        }
    }

    private fun replaceDate(currentMillis: Long, newDate: LocalDate): Long {
        val dateTime = Instant.ofEpochMilli(currentMillis).atZone(zoneId).toLocalDateTime()
        return dateTime
            .withYear(newDate.year)
            .withMonth(newDate.monthValue)
            .withDayOfMonth(newDate.dayOfMonth)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    private fun replaceTime(currentMillis: Long, newTime: LocalTime): Long {
        val dateTime = Instant.ofEpochMilli(currentMillis).atZone(zoneId).toLocalDateTime()
        return dateTime
            .withHour(newTime.hour)
            .withMinute(newTime.minute)
            .withSecond(0)
            .withNano(0)
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
    }

    companion object {
        fun factory(
            activityId: Long,
            getActivityUseCase: GetActivityUseCase,
            updateActivityUseCase: UpdateActivityUseCase,
            deleteActivityUseCase: DeleteActivityUseCase,
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    EditActivityViewModel(
                        activityId = activityId,
                        getActivityUseCase = getActivityUseCase,
                        updateActivityUseCase = updateActivityUseCase,
                        deleteActivityUseCase = deleteActivityUseCase,
                    )
                }
            }
        }
    }
}

sealed interface EditActivityEffect {
    data object NavigateBack : EditActivityEffect
}
