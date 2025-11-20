package com.coolfocus.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.coolfocus.app.service.UsageMonitorService

class App : Application() {
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UsageMonitorService.CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.notification_channel_description)
                enableVibration(true)
                setShowBadge(true)
            }
            
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
    
    companion object {
        lateinit var instance: App
            private set
    }
}