package com.example.digitaltwin.domain.activity.usecase

import com.example.digitaltwin.core.model.ActivityRecord
import com.example.digitaltwin.domain.activity.ActivityRepository

class UpdateActivityUseCase(
    private val repository: ActivityRepository,
) {
    suspend operator fun invoke(activity: ActivityRecord) {
        require(activity.name.isNotBlank()) { "Activity name must not be blank." }
        require(activity.endTime >= activity.startTime) { "End time must be after the start time." }

        check(repository.updateActivity(activity.copy(name = activity.name.trim()))) {
            "This activity could not be updated."
        }
    }
}
