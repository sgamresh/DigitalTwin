package com.example.digitaltwin.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitaltwin.domain.activity.usecase.DeleteActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetGroupedHistoryUseCase

@Composable
fun HistoryRoute(
    getGroupedHistoryUseCase: GetGroupedHistoryUseCase,
    deleteActivityUseCase: DeleteActivityUseCase,
    onEditActivity: (Long) -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModel.factory(
            getGroupedHistoryUseCase = getGroupedHistoryUseCase,
            deleteActivityUseCase = deleteActivityUseCase,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HistoryScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onEditActivity = onEditActivity,
        onOpenTimer = onOpenTimer,
        modifier = modifier,
    )
}
