package com.example.digitaltwin.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.digitaltwin.core.database.dao.ActivityDao
import com.example.digitaltwin.core.database.entity.ActivityEntity

@Database(
    entities = [ActivityEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TimeTrackerDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao

    companion object {
        const val DATABASE_NAME = "digital_twin.db"
    }
}

