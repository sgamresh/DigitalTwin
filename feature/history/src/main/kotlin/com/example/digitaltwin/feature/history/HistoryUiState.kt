package com.example.digitaltwin.feature.history

import com.example.digitaltwin.core.model.ActivityHistoryGroup

data class HistoryUiState(
    val historyGroups: List<ActivityHistoryGroup> = emptyList(),
    val deletingActivityIds: Set<Long> = emptySet(),
    val statusMessage: String? = null,
)
