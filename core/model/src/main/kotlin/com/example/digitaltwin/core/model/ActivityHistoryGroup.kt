package com.example.digitaltwin.core.model

import java.time.LocalDate

data class ActivityHistoryGroup(
    val date: LocalDate,
    val activities: List<ActivityRecord>,
)

