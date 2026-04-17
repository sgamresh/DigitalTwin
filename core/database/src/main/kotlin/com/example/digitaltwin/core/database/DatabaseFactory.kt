package com.example.digitaltwin.core.database

import android.content.Context
import androidx.room.Room

object DatabaseFactory {
    fun create(context: Context): TimeTrackerDatabase {
        return Room.databaseBuilder(
            context,
            TimeTrackerDatabase::class.java,
            TimeTrackerDatabase.DATABASE_NAME,
        ).build()
    }
}
