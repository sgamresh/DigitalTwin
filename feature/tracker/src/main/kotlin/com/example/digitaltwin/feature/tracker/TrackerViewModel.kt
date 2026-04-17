package com.example.digitaltwin.feature.tracker

import android.database.sqlite.SQLiteException
import android.os.SystemClock
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.digitaltwin.domain.activity.usecase.AddActivityUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackerViewModel(
    private val addActivityUseCase: AddActivityUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _uiState = MutableStateFlow(restoreUiState())
    val uiState: StateFlow<TrackerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var timerStartedAtElapsedRealtime: Long? =
        savedStateHandle.get<Long>(KEY_TIMER_STARTED_AT_ELAPSED_REALTIME)

    init {
        syncRunningTimer(persistSession = false)
        startTickerIfNeeded()
    }

    fun onAction(action: TrackerUiAction) {
        when (action) {
            is TrackerUiAction.ActivityNameChanged -> {
                updateUiState {
                    it.copy(
                        activityName = action.value,
                        activityNameError = null,
                    )
                }
            }

            TrackerUiAction.StartTimer -> startTimer()
            TrackerUiAction.StopTimer -> stopTimer()
            TrackerUiAction.SaveActivity -> saveActivity()
            TrackerUiAction.MessageShown -> updateUiState { it.copy(statusMessage = null) }
        }
    }

    fun onAppForegrounded() {
        syncRunningTimer(persistSession = false)
        startTickerIfNeeded()
    }

    fun onAppBackgrounded() {
        syncRunningTimer()
        stopTicker()
    }

    private fun startTimer() {
        val currentState = _uiState.value
        when {
            currentState.isSaving -> return
            currentState.isRunning || timerStartedAtElapsedRealtime != null -> {
                startTickerIfNeeded()
                return
            }

            currentState.startTime != null -> {
                updateUiState {
                    it.copy(statusMessage = "Save the current activity before starting another timer.")
                }
                return
            }
        }

        val startedAt = System.currentTimeMillis()
        timerStartedAtElapsedRealtime = SystemClock.elapsedRealtime()
        persistTimerStart()

        updateUiState {
            it.copy(
                isRunning = true,
                startTime = startedAt,
                endTime = null,
                elapsedMillis = 0L,
                activityNameError = null,
                statusMessage = null,
            )
        }

        startTickerIfNeeded()
    }

    private fun stopTimer() {
        val currentState = _uiState.value
        val startedAt = currentState.startTime
        val startedAtElapsedRealtime = timerStartedAtElapsedRealtime

        if (!currentState.isRunning || startedAt == null || startedAtElapsedRealtime == null) {
            updateUiState {
                it.copy(statusMessage = "Start the timer before trying to stop it.")
            }
            return
        }

        val elapsedMillis = currentElapsedMillis(startedAtElapsedRealtime)
        val stoppedAt = startedAt + elapsedMillis

        stopTicker()
        timerStartedAtElapsedRealtime = null
        persistTimerStart()

        updateUiState {
            it.copy(
                isRunning = false,
                endTime = stoppedAt,
                elapsedMillis = elapsedMillis,
                statusMessage = "Timer stopped. Add a name and save this activity to history.",
            )
        }
    }

    private fun saveActivity() {
        val currentState = _uiState.value
        if (currentState.isSaving) return

        val startedAt = currentState.startTime
        val stoppedAt = currentState.endTime
        val name = currentState.activityName.trim()

        if (startedAt == null || stoppedAt == null) {
            updateUiState { it.copy(statusMessage = "Start and stop the timer before saving.") }
            return
        }

        if (name.isBlank()) {
            updateUiState {
                it.copy(
                    activityNameError = "Activity name can't be empty.",
                    statusMessage = "Enter an activity name before saving.",
                )
            }
            return
        }

        updateUiState {
            it.copy(
                activityNameError = null,
                isSaving = true,
                statusMessage = null,
            )
        }

        viewModelScope.launch {
            try {
                addActivityUseCase(
                    name = name,
                    startTime = startedAt,
                    endTime = stoppedAt,
                )
                clearPersistedSession()
                _uiState.value = TrackerUiState(statusMessage = "Activity saved.")
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                updateUiState {
                    it.copy(
                        activityNameError = if (name.isBlank()) "Activity name can't be empty." else null,
                        isSaving = false,
                        statusMessage = exception.message ?: "Check the activity details and try again.",
                    )
                }
            } catch (exception: IllegalStateException) {
                updateUiState {
                    it.copy(
                        isSaving = false,
                        statusMessage = exception.message ?: "This activity could not be saved.",
                    )
                }
            } catch (exception: SQLiteException) {
                updateUiState {
                    it.copy(
                        isSaving = false,
                        statusMessage = "The activity could not be saved right now. Please try again.",
                    )
                }
            } catch (exception: Exception) {
                updateUiState {
                    it.copy(
                        isSaving = false,
                        statusMessage = "Something went wrong while saving the activity.",
                    )
                }
            }
        }
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }

    private fun startTickerIfNeeded() {
        val currentState = _uiState.value
        val startedAt = currentState.startTime ?: return
        val startedAtElapsedRealtime = timerStartedAtElapsedRealtime ?: return

        if (!currentState.isRunning || currentState.isSaving || timerJob?.isActive == true) {
            return
        }

        timerJob = viewModelScope.launch(Dispatchers.Default) {
            while (isActive) {
                val elapsedMillis = currentElapsedMillis(startedAtElapsedRealtime)
                updateUiState(persistSession = false) { state ->
                    if (!state.isRunning || state.startTime != startedAt) {
                        state
                    } else {
                        state.copy(elapsedMillis = elapsedMillis)
                    }
                }
                delay(TIMER_UPDATE_INTERVAL_MILLIS)
            }
        }
    }

    private fun stopTicker() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun syncRunningTimer(persistSession: Boolean = true) {
        val currentState = _uiState.value
        val startedAt = currentState.startTime ?: return
        val startedAtElapsedRealtime = timerStartedAtElapsedRealtime ?: return

        val elapsedMillis = currentElapsedMillis(startedAtElapsedRealtime)
        updateUiState(persistSession = persistSession) { state ->
            if (state.startTime != startedAt) {
                state
            } else {
                state.copy(elapsedMillis = elapsedMillis)
            }
        }
    }

    private fun restoreUiState(): TrackerUiState {
        return TrackerUiState(
            activityName = savedStateHandle.get<String>(KEY_ACTIVITY_NAME).orEmpty(),
            isRunning = savedStateHandle.get<Boolean>(KEY_IS_RUNNING) ?: false,
            startTime = savedStateHandle.get(KEY_START_TIME),
            endTime = savedStateHandle.get(KEY_END_TIME),
            elapsedMillis = savedStateHandle.get<Long>(KEY_ELAPSED_MILLIS) ?: 0L,
        )
    }

    private fun updateUiState(
        persistSession: Boolean = true,
        transform: (TrackerUiState) -> TrackerUiState,
    ) {
        _uiState.update { current ->
            val updated = transform(current)
            if (persistSession) {
                persistSession(updated)
            }
            updated
        }
    }

    private fun persistSession(state: TrackerUiState) {
        savedStateHandle[KEY_ACTIVITY_NAME] = state.activityName
        savedStateHandle[KEY_IS_RUNNING] = state.isRunning
        savedStateHandle[KEY_ELAPSED_MILLIS] = state.elapsedMillis
        setOrRemove(KEY_START_TIME, state.startTime)
        setOrRemove(KEY_END_TIME, state.endTime)
        persistTimerStart()
    }

    private fun clearPersistedSession() {
        savedStateHandle.remove<String>(KEY_ACTIVITY_NAME)
        savedStateHandle.remove<Boolean>(KEY_IS_RUNNING)
        savedStateHandle.remove<Long>(KEY_ELAPSED_MILLIS)
        savedStateHandle.remove<Long>(KEY_START_TIME)
        savedStateHandle.remove<Long>(KEY_END_TIME)
        savedStateHandle.remove<Long>(KEY_TIMER_STARTED_AT_ELAPSED_REALTIME)
    }

    private fun persistTimerStart() {
        setOrRemove(KEY_TIMER_STARTED_AT_ELAPSED_REALTIME, timerStartedAtElapsedRealtime)
    }

    private fun setOrRemove(key: String, value: Long?) {
        if (value == null) {
            savedStateHandle.remove<Long>(key)
        } else {
            savedStateHandle[key] = value
        }
    }

    private fun currentElapsedMillis(startedAtElapsedRealtime: Long): Long {
        return (SystemClock.elapsedRealtime() - startedAtElapsedRealtime).coerceAtLeast(0L)
    }

    companion object {
        private const val TIMER_UPDATE_INTERVAL_MILLIS = 1_000L
        private const val KEY_ACTIVITY_NAME = "tracker.activityName"
        private const val KEY_IS_RUNNING = "tracker.isRunning"
        private const val KEY_START_TIME = "tracker.startTime"
        private const val KEY_END_TIME = "tracker.endTime"
        private const val KEY_ELAPSED_MILLIS = "tracker.elapsedMillis"
        private const val KEY_TIMER_STARTED_AT_ELAPSED_REALTIME = "tracker.startedAtElapsedRealtime"

        fun factory(addActivityUseCase: AddActivityUseCase): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    TrackerViewModel(
                        addActivityUseCase = addActivityUseCase,
                        savedStateHandle = createSavedStateHandle(),
                    )
                }
            }
        }
    }
}
