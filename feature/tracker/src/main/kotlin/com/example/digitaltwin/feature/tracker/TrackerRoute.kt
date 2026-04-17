package com.example.digitaltwin.feature.tracker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.digitaltwin.domain.activity.usecase.AddActivityUseCase

@Composable
fun TrackerRoute(
    addActivityUseCase: AddActivityUseCase,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: TrackerViewModel = viewModel(
        factory = TrackerViewModel.factory(addActivityUseCase = addActivityUseCase),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> viewModel.onAppForegrounded()
                Lifecycle.Event.ON_STOP -> viewModel.onAppBackgrounded()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.onAppForegrounded()

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.onAppBackgrounded()
        }
    }

    TrackerScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onOpenHistory = onOpenHistory,
        modifier = modifier,
    )
}
