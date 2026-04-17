package com.example.digitaltwin.domain.activity

import com.example.digitaltwin.core.model.ActivityRecord
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun observeActivities(): Flow<List<ActivityRecord>>

    suspend fun getActivity(id: Long): ActivityRecord?

    suspend fun addActivity(activity: ActivityRecord): Long

    suspend fun updateActivity(activity: ActivityRecord): Boolean

    suspend fun deleteActivity(id: Long): Boolean
}
