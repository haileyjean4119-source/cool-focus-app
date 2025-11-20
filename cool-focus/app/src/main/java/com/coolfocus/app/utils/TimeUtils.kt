package com.coolfocus.app.utils

import java.util.concurrent.TimeUnit

object TimeUtils {
    
    fun formatDuration(millis: Long): String {
        if (millis < 0) return "00:00"
        
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            else -> String.format("%02d:%02d", minutes, seconds)
        }
    }
    
    fun formatDurationShort(millis: Long): String {
        if (millis < 0) return "0m"
        
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }
    
    fun formatDurationMinutes(millis: Long): Int {
        return TimeUnit.MILLISECONDS.toMinutes(millis).toInt()
    }
    
    fun minutesToMillis(minutes: Int): Long {
        return TimeUnit.MINUTES.toMillis(minutes.toLong())
    }
    
    fun secondsToMillis(seconds: Int): Long {
        return TimeUnit.SECONDS.toMillis(seconds.toLong())
    }
    
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val day1 = TimeUnit.MILLISECONDS.toDays(timestamp1)
        val day2 = TimeUnit.MILLISECONDS.toDays(timestamp2)
        return day1 == day2
    }
    
    fun getStartOfDay(timestamp: Long): Long {
        return timestamp - (timestamp % TimeUnit.DAYS.toMillis(1))
    }
    
    fun getEndOfDay(timestamp: Long): Long {
        return getStartOfDay(timestamp) + TimeUnit.DAYS.toMillis(1) - 1
    }
}