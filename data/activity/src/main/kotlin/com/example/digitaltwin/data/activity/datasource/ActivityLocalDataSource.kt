package com.example.digitaltwin.data.activity.datasource

import com.example.digitaltwin.core.database.dao.ActivityDao
import com.example.digitaltwin.core.database.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

class ActivityLocalDataSource(
    private val activityDao: ActivityDao,
) {
    fun observeAll(): Flow<List<ActivityEntity>> = activityDao.observeAll()

    suspend fun getById(id: Long): ActivityEntity? = activityDao.getById(id)

    suspend fun insert(activity: ActivityEntity): Long = activityDao.insert(activity)

    suspend fun update(activity: ActivityEntity): Int = activityDao.update(activity)

    suspend fun deleteById(id: Long): Int = activityDao.deleteById(id)
}
