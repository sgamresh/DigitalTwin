package com.example.digitaltwin.di

import android.content.Context
import com.example.digitaltwin.core.database.DatabaseFactory
import com.example.digitaltwin.data.activity.ActivityRepositoryImpl
import com.example.digitaltwin.data.activity.datasource.ActivityLocalDataSource
import com.example.digitaltwin.domain.activity.usecase.AddActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.DeleteActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetActivityUseCase
import com.example.digitaltwin.domain.activity.usecase.GetGroupedHistoryUseCase
import com.example.digitaltwin.domain.activity.usecase.UpdateActivityUseCase

class AppContainer(
    context: Context,
) {
    private val database = DatabaseFactory.create(context)
    private val localDataSource = ActivityLocalDataSource(database.activityDao())
    private val repository = ActivityRepositoryImpl(localDataSource)

    val addActivityUseCase = AddActivityUseCase(repository)
    val deleteActivityUseCase = DeleteActivityUseCase(repository)
    val getActivityUseCase = GetActivityUseCase(repository)
    val getGroupedHistoryUseCase = GetGroupedHistoryUseCase(repository)
    val updateActivityUseCase = UpdateActivityUseCase(repository)
}

