package com.example.digitaltwin.feature.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitaltwin.domain.activity.usecase.DeleteActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.UpdateActivityUseCase
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EditActivityRoute(
    activityId: Long,
    getActivityUseCase: GetActivityUseCase,
    updateActivityUseCase: UpdateActivityUseCase,
    deleteActivityUseCase: DeleteActivityUseCase,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: EditActivityViewModel = viewModel(
        factory = EditActivityViewModel.factory(
            activityId = activityId,
            getActivityUseCase = getActivityUseCase,
            updateActivityUseCase = updateActivityUseCase,
            deleteActivityUseCase = deleteActivityUseCase,
        ),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                EditActivityEffect.NavigateBack -> onDone()
            }
        }
    }

    EditActivityScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onDismiss = onDone,
        modifier = modifier,
    )
}
