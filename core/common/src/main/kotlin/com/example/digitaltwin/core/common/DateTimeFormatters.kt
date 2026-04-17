package com.example.digitaltwin.core.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeFormatters {
    fun formatTimerDuration(durationMillis: Long): String {
        val totalSeconds = durationMillis.coerceAtLeast(0L) / 1_000L
        val hours = totalSeconds / 3_600L
        val minutes = (totalSeconds % 3_600L) / 60L
        val seconds = totalSeconds % 60L
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun formatDuration(durationMillis: Long): String {
        val totalMinutes = durationMillis.coerceAtLeast(0L) / 60_000L
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return String.format(Locale.getDefault(), "%02dh %02dm", hours, minutes)
    }

    fun formatDate(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", locale)
        return date.format(formatter)
    }

    fun formatDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        return formatDate(toLocalDate(epochMillis, zoneId))
    }

    fun formatTime(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalTime().format(formatter)
    }

    fun formatTimeRange(
        startTimeMillis: Long,
        endTimeMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): String {
        return "${formatTime(startTimeMillis, zoneId)} - ${formatTime(endTimeMillis, zoneId)}"
    }

    fun toLocalDate(epochMillis: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
        return Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
    }
}
