package com.example.digitaltwin.domain.activity.usecase

import com.example.digitaltwin.core.model.ActivityRecord
import com.example.digitaltwin.domain.activity.ActivityRepository

class GetActivityUseCase(
    private val repository: ActivityRepository,
) {
    suspend operator fun invoke(id: Long): ActivityRecord? {
        return repository.getActivity(id)
    }
}

