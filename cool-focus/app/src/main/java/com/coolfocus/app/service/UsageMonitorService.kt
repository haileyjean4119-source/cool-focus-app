package com.coolfocus.app.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.coolfocus.app.R
import com.coolfocus.app.receiver.ScreenStateReceiver
import com.coolfocus.app.ui.MainActivity
import com.coolfocus.app.utils.PreferencesManager
import com.coolfocus.app.utils.TimeUtils
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class UsageMonitorService : LifecycleService() {
    
    private lateinit var preferencesManager: PreferencesManager
    private var screenStateReceiver: ScreenStateReceiver? = null
    private var wakeLock: PowerManager.WakeLock? = null
    
    // Timer variables
    private var currentStartTime: Long = 0
    private var currentUsageTime: Long = 0
    private var todayTotalTime: Long = 0
    private var lastBreakTime: Long = 0
    private var reminderCount: Int = 0
    private var isScreenOn: Boolean = false
    private var isPaused: Boolean = false
    
    // Coroutine job
    private var monitorJob: Job? = null
    
    companion object {
        const val CHANNEL_ID = "usage_monitor_channel"
        const val NOTIFICATION_ID = 1
        
        @Volatile
        var isRunning: Boolean = false
            private set
        
        @Volatile
        var currentUsageTime: Long = 0
            private set
        
        @Volatile
        var todayTotalTime: Long = 0
            private set
    }
    
    private val binder = LocalBinder()
    
    inner class LocalBinder : Binder() {
        fun getService(): UsageMonitorService = this@UsageMonitorService
    }
    
    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        
        // Acquire wake lock to keep service running
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CoolFocus::UsageMonitor")
        wakeLock?.acquire()
        
        registerScreenReceiver()
        startMonitoring()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        
        startForeground(NOTIFICATION_ID, createNotification())
        isRunning = true
        
        return START_STICKY
    }
    
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        
        monitorJob?.cancel()
        screenStateReceiver?.let {
            unregisterReceiver(it)
        }
        
        wakeLock?.release()
    }
    
    private fun registerScreenReceiver() {
        screenStateReceiver = ScreenStateReceiver { action ->
            when (action) {
                Intent.ACTION_SCREEN_ON -> {
                    isScreenOn = true
                    if (!isPaused) {
                        startCurrentSession()
                    }
                }
                Intent.ACTION_SCREEN_OFF -> {
                    isScreenOn = false
                    pauseCurrentSession()
                    checkForBreak()
                }
                Intent.ACTION_USER_PRESENT -> {
                    isScreenOn = true
                    if (!isPaused) {
                        startCurrentSession()
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenStateReceiver, filter)
    }
    
    private fun startMonitoring() {
        monitorJob?.cancel()
        monitorJob = lifecycleScope.launch {
            while (isActive) {
                if (isScreenOn && !isPaused) {
                    updateUsageTime()
                    checkReminderThreshold()
                }
                updateNotification()
                delay(1000) // Check every second
            }
        }
    }
    
    private fun startCurrentSession() {
        if (currentStartTime == 0L) {
            currentStartTime = System.currentTimeMillis()
        }
    }
    
    private fun pauseCurrentSession() {
        if (currentStartTime > 0) {
            val sessionEndTime = System.currentTimeMillis()
            val sessionDuration = sessionEndTime - currentStartTime
            currentUsageTime += sessionDuration
            todayTotalTime += sessionDuration
            currentStartTime = 0
        }
    }
    
    private fun updateUsageTime() {
        if (isScreenOn && currentStartTime > 0) {
            val now = System.currentTimeMillis()
            currentUsageTime = now - currentStartTime
        }
    }
    
    private fun checkForBreak() {
        val effectiveBreakTime = preferencesManager.getEffectiveBreakTime()
        val now = System.currentTimeMillis()
        
        if (now - lastBreakTime >= TimeUnit.MINUTES.toMillis(effectiveBreakTime.toLong())) {
            // This is a valid break
            if (currentUsageTime > 0) {
                saveUsageSession()
            }
            resetCurrentUsage()
            lastBreakTime = now
        }
    }
    
    private fun checkReminderThreshold() {
        val thresholdMinutes = preferencesManager.getReminderThreshold()
        val thresholdMillis = TimeUnit.MINUTES.toMillis(thresholdMinutes.toLong())
        
        if (currentUsageTime >= thresholdMillis && reminderCount == 0) {
            showReminder()
            reminderCount++
        }
    }
    
    private fun showReminder() {
        val reminderIntent = Intent(this, ReminderActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("usage_time", currentUsageTime)
        }
        
        try {
            startActivity(reminderIntent)
        } catch (e: Exception) {
            // Fallback to notification if activity can't be started
            showReminderNotification()
        }
    }
    
    private fun showReminderNotification() {
        val reminderNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.reminder_title))
            .setContentText(getString(R.string.reminder_message, TimeUtils.formatDuration(currentUsageTime)))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(2, reminderNotification)
    }
    
    private fun resetCurrentUsage() {
        currentUsageTime = 0
        currentStartTime = 0
        reminderCount = 0
    }
    
    private fun saveUsageSession() {
        // Save to preferences or database
        preferencesManager.saveUsageData(currentUsageTime, todayTotalTime)
    }
    
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getNotificationContent())
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
        
        // Update companion object values
        Companion.currentUsageTime = this.currentUsageTime
        Companion.todayTotalTime = this.todayTotalTime
    }
    
    private fun getNotificationContent(): String {
        return if (isScreenOn) {
            getString(R.string.reminder_notification_content, TimeUtils.formatDuration(currentUsageTime))
        } else {
            getString(R.string.status_service_running)
        }
    }
}