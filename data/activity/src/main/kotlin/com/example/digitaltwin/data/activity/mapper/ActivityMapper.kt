package com.example.digitaltwin.data.activity.mapper

import com.example.digitaltwin.core.database.entity.ActivityEntity
import com.example.digitaltwin.core.model.ActivityRecord

object ActivityMapper {
    fun toDomain(entity: ActivityEntity): ActivityRecord {
        return ActivityRecord(
            id = entity.id,
            name = entity.name,
            startTime = entity.startTime,
            endTime = entity.endTime,
        )
    }

    fun toEntity(record: ActivityRecord): ActivityEntity {
        return ActivityEntity(
            id = record.id,
            name = record.name,
            startTime = record.startTime,
            endTime = record.endTime,
        )
    }
}

