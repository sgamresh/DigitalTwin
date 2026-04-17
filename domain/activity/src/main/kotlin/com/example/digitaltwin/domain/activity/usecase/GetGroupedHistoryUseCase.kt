package com.example.digitaltwin.domain.activity.usecase

import com.example.digitaltwin.core.model.ActivityHistoryGroup
import com.example.digitaltwin.domain.activity.ActivityRepository
import java.time.Instant
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetGroupedHistoryUseCase(
    private val repository: ActivityRepository,
) {
    operator fun invoke(zoneId: ZoneId = ZoneId.systemDefault()): Flow<List<ActivityHistoryGroup>> {
        return repository.observeActivities().map { activities ->
            activities
                .sortedByDescending { it.startTime }
                .groupBy { Instant.ofEpochMilli(it.startTime).atZone(zoneId).toLocalDate() }
                .entries
                .sortedByDescending { it.key }
                .map { (date, items) ->
                    ActivityHistoryGroup(
                        date = date,
                        activities = items.sortedByDescending { it.startTime },
                    )
                }
        }
    }
}

