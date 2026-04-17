package com.example.digitaltwin.domain.activity.usecase

import com.example.digitaltwin.domain.activity.ActivityRepository

class DeleteActivityUseCase(
    private val repository: ActivityRepository,
) {
    suspend operator fun invoke(id: Long) {
        require(id > 0L) { "Activity id is invalid." }
        check(repository.deleteActivity(id)) { "This activity no longer exists." }
    }
}
