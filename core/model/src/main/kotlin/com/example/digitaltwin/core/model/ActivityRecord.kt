package com.example.digitaltwin.core.model

data class ActivityRecord(
    val id: Long = 0L,
    val name: String,
    val startTime: Long,
    val endTime: Long,
) {
    val durationMillis: Long
        get() = (endTime - startTime).coerceAtLeast(0L)
}

