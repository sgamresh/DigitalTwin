package com.example.digitaltwin.feature.history

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.digitaltwin.domain.activity.usecase.DeleteActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetGroupedHistoryUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryViewModel(
    getGroupedHistoryUseCase: GetGroupedHistoryUseCase,
    private val deleteActivityUseCase: DeleteActivityUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getGroupedHistoryUseCase()
                .catch {
                    _uiState.update {
                        it.copy(statusMessage = "Activity history could not be loaded right now.")
                    }
                }
                .collectLatest { groups ->
                    _uiState.update { it.copy(historyGroups = groups) }
                }
        }
    }

    fun onAction(action: HistoryUiAction) {
        when (action) {
            is HistoryUiAction.DeleteActivity -> deleteActivity(action.activityId)
            HistoryUiAction.MessageShown -> _uiState.update { it.copy(statusMessage = null) }
        }
    }

    private fun deleteActivity(activityId: Long) {
        if (_uiState.value.deletingActivityIds.contains(activityId)) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    deletingActivityIds = it.deletingActivityIds + activityId,
                    statusMessage = null,
                )
            }

            try {
                deleteActivityUseCase(activityId)
                _uiState.update { it.copy(statusMessage = "Activity deleted.") }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: IllegalArgumentException) {
                _uiState.update {
                    it.copy(
                        statusMessage = exception.message ?: "The activity could not be deleted.",
                    )
                }
            } catch (exception: IllegalStateException) {
                _uiState.update {
                    it.copy(
                        statusMessage = exception.message ?: "The activity could not be deleted.",
                    )
                }
            } catch (exception: SQLiteException) {
                _uiState.update {
                    it.copy(statusMessage = "The activity could not be deleted right now. Please try again.")
                }
            } catch (exception: Exception) {
                _uiState.update {
                    it.copy(statusMessage = "Something went wrong while deleting the activity.")
                }
            } finally {
                _uiState.update {
                    it.copy(deletingActivityIds = it.deletingActivityIds - activityId)
                }
            }
        }
    }

    companion object {
        fun factory(
            getGroupedHistoryUseCase: GetGroupedHistoryUseCase,
            deleteActivityUseCase: DeleteActivityUseCase,
        ): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    HistoryViewModel(
                        getGroupedHistoryUseCase = getGroupedHistoryUseCase,
                        deleteActivityUseCase = deleteActivityUseCase,
                    )
                }
            }
        }
    }
}
