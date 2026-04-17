package com.example.digitaltwin.data.activity

import com.example.digitaltwin.core.model.ActivityRecord
import com.example.digitaltwin.data.activity.datasource.ActivityLocalDataSource
import com.example.digitaltwin.data.activity.mapper.ActivityMapper
import com.example.digitaltwin.domain.activity.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val localDataSource: ActivityLocalDataSource,
) : ActivityRepository {
    override fun observeActivities(): Flow<List<ActivityRecord>> {
        return localDataSource.observeAll().map { activities ->
            activities.map(ActivityMapper::toDomain)
        }
    }

    override suspend fun getActivity(id: Long): ActivityRecord? {
        return localDataSource.getById(id)?.let(ActivityMapper::toDomain)
    }

    override suspend fun addActivity(activity: ActivityRecord): Long {
        return localDataSource.insert(ActivityMapper.toEntity(activity))
    }

    override suspend fun updateActivity(activity: ActivityRecord): Boolean {
        return localDataSource.update(ActivityMapper.toEntity(activity)) > 0
    }

    override suspend fun deleteActivity(id: Long): Boolean {
        return localDataSource.deleteById(id) > 0
    }
}
