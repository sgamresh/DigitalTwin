package com.example.digitaltwin.domain.activity.usecase

import com.example.digitaltwin.core.model.ActivityRecord
import com.example.digitaltwin.domain.activity.ActivityRepository

class AddActivityUseCase(
    private val repository: ActivityRepository,
) {
    suspend operator fun invoke(
        name: String,
        startTime: Long,
        endTime: Long,
    ): Long {
        require(name.isNotBlank()) { "Activity name must not be blank." }
        require(endTime >= startTime) { "End time must be after the start time." }

        val activityId = repository.addActivity(
            ActivityRecord(
                name = name.trim(),
                startTime = startTime,
                endTime = endTime,
            ),
        )
        check(activityId > 0L) { "Activity could not be saved." }
        return activityId
    }
}
