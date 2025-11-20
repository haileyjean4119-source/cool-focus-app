package com.coolfocus.app.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.coolfocus.app.R
import com.coolfocus.app.databinding.ActivityReminderBinding
import com.coolfocus.app.service.UsageMonitorService
import com.coolfocus.app.utils.TimeUtils

class ReminderActivity : Activity() {
    
    private lateinit var binding: ActivityReminderBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Make this activity appear as a dialog
        setTheme(android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar)
        
        val usageTime = intent.getLongExtra("usage_time", 0)
        
        setupUI(usageTime)
    }
    
    private fun setupUI(usageTime: Long) {
        binding.reminderMessage.text = getString(
            R.string.reminder_message, 
            TimeUtils.formatDuration(usageTime)
        )
        
        binding.takeBreakButton.setOnClickListener {
            // User chooses to take a break
            val serviceIntent = Intent(this, UsageMonitorService::class.java)
            stopService(serviceIntent)
            finish()
        }
        
        binding.continueButton.setOnClickListener {
            // User chooses to continue
            finish()
        }
    }
    
    override fun onBackPressed() {
        // Don't allow back button to dismiss the reminder
        // User must make a choice
    }
}