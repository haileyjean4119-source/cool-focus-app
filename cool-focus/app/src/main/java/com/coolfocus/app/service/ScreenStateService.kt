package com.coolfocus.app.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.coolfocus.app.utils.PreferencesManager

class ScreenStateService : Service() {
    
    private lateinit var preferencesManager: PreferencesManager
    private var screenReceiver: BroadcastReceiver? = null
    
    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
        registerScreenReceiver()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        screenReceiver?.let {
            unregisterReceiver(it)
        }
    }
    
    private fun registerScreenReceiver() {
        screenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    Intent.ACTION_SCREEN_ON -> {
                        Log.d("ScreenStateService", "Screen ON")
                        handleScreenOn()
                    }
                    Intent.ACTION_SCREEN_OFF -> {
                        Log.d("ScreenStateService", "Screen OFF")
                        handleScreenOff()
                    }
                    Intent.ACTION_USER_PRESENT -> {
                        Log.d("ScreenStateService", "User Present")
                        handleUserPresent()
                    }
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenReceiver, filter)
    }
    
    private fun handleScreenOn() {
        // Handle screen on event
        val usageIntent = Intent(this, UsageMonitorService::class.java)
        startService(usageIntent)
    }
    
    private fun handleScreenOff() {
        // Handle screen off event
        // Service will handle the break logic
    }
    
    private fun handleUserPresent() {
        // Handle user unlock event
        val usageIntent = Intent(this, UsageMonitorService::class.java)
        startService(usageIntent)
    }
}