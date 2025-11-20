package com.coolfocus.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.coolfocus.app.service.UsageMonitorService

class ScreenStateReceiver : BroadcastReceiver() {
    
    private var callback: ((String) -> Unit)? = null
    
    constructor()
    
    constructor(callback: (String) -> Unit) {
        this.callback = callback
    }
    
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> {
                Log.d("ScreenStateReceiver", "Screen turned ON")
                callback?.invoke(Intent.ACTION_SCREEN_ON)
                startUsageService(context)
            }
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("ScreenStateReceiver", "Screen turned OFF")
                callback?.invoke(Intent.ACTION_SCREEN_OFF)
            }
            Intent.ACTION_USER_PRESENT -> {
                Log.d("ScreenStateReceiver", "User present")
                callback?.invoke(Intent.ACTION_USER_PRESENT)
                startUsageService(context)
            }
        }
    }
    
    private fun startUsageService(context: Context?) {
        context?.let {
            val serviceIntent = Intent(it, UsageMonitorService::class.java)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                it.startForegroundService(serviceIntent)
            } else {
                it.startService(serviceIntent)
            }
        }
    }
}