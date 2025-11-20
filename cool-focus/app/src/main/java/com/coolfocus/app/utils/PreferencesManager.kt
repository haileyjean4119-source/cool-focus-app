package com.coolfocus.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        PreferenceManager.getDefaultSharedPreferences(context)
    
    // Reminder settings
    fun getReminderThreshold(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_THRESHOLD, DEFAULT_REMINDER_THRESHOLD)
    }
    
    fun setReminderThreshold(minutes: Int) {
        sharedPreferences.edit().putInt(KEY_REMINDER_THRESHOLD, minutes).apply()
    }
    
    fun getEffectiveBreakTime(): Int {
        return sharedPreferences.getInt(KEY_EFFECTIVE_BREAK_TIME, DEFAULT_EFFECTIVE_BREAK_TIME)
    }
    
    fun setEffectiveBreakTime(minutes: Int) {
        sharedPreferences.edit().putInt(KEY_EFFECTIVE_BREAK_TIME, minutes).apply()
    }
    
    fun getReminderFrequency(): ReminderFrequency {
        val ordinal = sharedPreferences.getInt(KEY_REMINDER_FREQUENCY, 
            ReminderFrequency.SINGLE.ordinal)
        return ReminderFrequency.values().getOrNull(ordinal) ?: ReminderFrequency.SINGLE
    }
    
    fun setReminderFrequency(frequency: ReminderFrequency) {
        sharedPreferences.edit().putInt(KEY_REMINDER_FREQUENCY, frequency.ordinal).apply()
    }
    
    fun getReminderInterval(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_INTERVAL, DEFAULT_REMINDER_INTERVAL)
    }
    
    fun setReminderInterval(seconds: Int) {
        sharedPreferences.edit().putInt(KEY_REMINDER_INTERVAL, seconds).apply()
    }
    
    fun getReminderRepeatCount(): Int {
        return sharedPreferences.getInt(KEY_REMINDER_REPEAT_COUNT, DEFAULT_REMINDER_REPEAT_COUNT)
    }
    
    fun setReminderRepeatCount(count: Int) {
        sharedPreferences.edit().putInt(KEY_REMINDER_REPEAT_COUNT, count).apply()
    }
    
    // Notification settings
    fun getNotificationMode(): NotificationMode {
        val ordinal = sharedPreferences.getInt(KEY_NOTIFICATION_MODE, 
            NotificationMode.SOUND_AND_VIBRATION.ordinal)
        return NotificationMode.values().getOrNull(ordinal) ?: NotificationMode.SOUND_AND_VIBRATION
    }
    
    fun setNotificationMode(mode: NotificationMode) {
        sharedPreferences.edit().putInt(KEY_NOTIFICATION_MODE, mode.ordinal).apply()
    }
    
    fun isNotificationSoundEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_SOUND, true)
    }
    
    fun setNotificationSoundEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_SOUND, enabled).apply()
    }
    
    fun isNotificationVibrationEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_VIBRATION, true)
    }
    
    fun setNotificationVibrationEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_VIBRATION, enabled).apply()
    }
    
    // Daily goal
    fun getDailyGoal(): Int {
        return sharedPreferences.getInt(KEY_DAILY_GOAL, DEFAULT_DAILY_GOAL)
    }
    
    fun setDailyGoal(minutes: Int) {
        sharedPreferences.edit().putInt(KEY_DAILY_GOAL, minutes).apply()
    }
    
    // Usage data
    fun saveUsageData(currentUsage: Long, todayTotal: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(KEY_CURRENT_USAGE, currentUsage)
        editor.putLong(KEY_TODAY_TOTAL, todayTotal)
        editor.apply()
    }
    
    fun getCurrentUsage(): Long {
        return sharedPreferences.getLong(KEY_CURRENT_USAGE, 0)
    }
    
    fun getTodayTotal(): Long {
        return sharedPreferences.getLong(KEY_TODAY_TOTAL, 0)
    }
    
    fun saveBreakCount(count: Int) {
        sharedPreferences.edit().putInt(KEY_BREAK_COUNT, count).apply()
    }
    
    fun getBreakCount(): Int {
        return sharedPreferences.getLong(KEY_BREAK_COUNT, 0).toInt()
    }
    
    fun saveIgnoreCount(count: Int) {
        sharedPreferences.edit().putInt(KEY_IGNORE_COUNT, count).apply()
    }
    
    fun getIgnoreCount(): Int {
        return sharedPreferences.getLong(KEY_IGNORE_COUNT, 0).toInt()
    }
    
    fun saveMaxContinuousUsage(time: Long) {
        sharedPreferences.edit().putLong(KEY_MAX_CONTINUOUS_USAGE, time).apply()
    }
    
    fun getMaxContinuousUsage(): Long {
        return sharedPreferences.getLong(KEY_MAX_CONTINUOUS_USAGE, 0)
    }
    
    fun resetDailyData() {
        val editor = sharedPreferences.edit()
        editor.remove(KEY_TODAY_TOTAL)
        editor.remove(KEY_BREAK_COUNT)
        editor.remove(KEY_IGNORE_COUNT)
        editor.remove(KEY_MAX_CONTINUOUS_USAGE)
        editor.apply()
    }
    
    enum class ReminderFrequency {
        SINGLE,         // 单次提醒
        MULTIPLE,       // 连续提醒X次
        CONTINUOUS      // 持续提醒直到休息
    }
    
    enum class NotificationMode {
        SILENT,         // 静音
        VIBRATION_ONLY, // 仅震动
        SOUND_ONLY,     // 仅铃声
        SOUND_AND_VIBRATION // 铃声+震动
    }
    
    companion object {
        private const val KEY_REMINDER_THRESHOLD = "reminder_threshold"
        private const val KEY_EFFECTIVE_BREAK_TIME = "effective_break_time"
        private const val KEY_REMINDER_FREQUENCY = "reminder_frequency"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_REMINDER_REPEAT_COUNT = "reminder_repeat_count"
        
        private const val KEY_NOTIFICATION_MODE = "notification_mode"
        private const val KEY_NOTIFICATION_SOUND = "notification_sound"
        private const val KEY_NOTIFICATION_VIBRATION = "notification_vibration"
        
        private const val KEY_DAILY_GOAL = "daily_goal"
        
        private const val KEY_CURRENT_USAGE = "current_usage"
        private const val KEY_TODAY_TOTAL = "today_total"
        private const val KEY_BREAK_COUNT = "break_count"
        private const val KEY_IGNORE_COUNT = "ignore_count"
        private const val KEY_MAX_CONTINUOUS_USAGE = "max_continuous_usage"
        
        private const val DEFAULT_REMINDER_THRESHOLD = 30 // 30 minutes
        private const val DEFAULT_EFFECTIVE_BREAK_TIME = 5 // 5 minutes
        private const val DEFAULT_REMINDER_INTERVAL = 30 // 30 seconds
        private const val DEFAULT_REMINDER_REPEAT_COUNT = 3 // 3 times
        private const val DEFAULT_DAILY_GOAL = 240 // 4 hours
    }
}