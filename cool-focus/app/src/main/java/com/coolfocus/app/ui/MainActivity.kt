package com.coolfocus.app.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.coolfocus.app.R
import com.coolfocus.app.databinding.ActivityMainBinding
import com.coolfocus.app.receiver.ScreenStateReceiver
import com.coolfocus.app.service.UsageMonitorService
import com.coolfocus.app.utils.PreferencesManager
import com.coolfocus.app.utils.TimeUtils
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private var screenStateReceiver: ScreenStateReceiver? = null
    private var updateJob: Job? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        
        setupUI()
        checkPermissions()
        startMonitoring()
        registerScreenReceiver()
    }
    
    private fun setupUI() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
        
        // Setup bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_statistics -> {
                    startActivity(Intent(this, StatisticsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        
        // Setup control button
        binding.controlButton.setOnClickListener {
            if (UsageMonitorService.isRunning) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }
        
        // Setup permission guide button
        binding.permissionGuideButton.setOnClickListener {
            startActivity(Intent(this, PermissionGuideActivity::class.java))
        }
    }
    
    private fun checkPermissions() {
        // Check if we need to show permission guide
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                binding.permissionGuideButton.visibility = android.view.View.VISIBLE
            } else {
                binding.permissionGuideButton.visibility = android.view.View.GONE
            }
        }
    }
    
    private fun startMonitoring() {
        val serviceIntent = Intent(this, UsageMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
        
        binding.controlButton.text = getString(R.string.stop_monitoring)
        binding.statusText.text = getString(R.string.status_monitoring)
        
        startUpdatingUI()
    }
    
    private fun stopMonitoring() {
        val serviceIntent = Intent(this, UsageMonitorService::class.java)
        stopService(serviceIntent)
        
        binding.controlButton.text = getString(R.string.start_monitoring)
        binding.statusText.text = getString(R.string.status_not_monitoring)
        
        stopUpdatingUI()
    }
    
    private fun startUpdatingUI() {
        updateJob?.cancel()
        updateJob = lifecycleScope.launch {
            while (isActive) {
                updateUI()
                delay(1000) // Update every second
            }
        }
    }
    
    private fun stopUpdatingUI() {
        updateJob?.cancel()
        updateJob = null
    }
    
    private fun updateUI() {
        val currentUsage = UsageMonitorService.currentUsageTime
        val todayTotal = UsageMonitorService.todayTotalTime
        val dailyGoal = preferencesManager.getDailyGoal()
        
        runOnUiThread {
            // Update current usage display
            binding.currentUsageTime.text = TimeUtils.formatDuration(currentUsage)
            
            // Update circular progress
            val threshold = preferencesManager.getReminderThreshold()
            val progress = if (threshold > 0) {
                (currentUsage.toFloat() / (threshold * 60 * 1000) * 100).coerceAtMost(100f)
            } else {
                0f
            }
            binding.usageProgress.progress = progress
            
            // Change progress color based on usage
            val progressColor = when {
                progress >= 100 -> getColor(R.color.progress_danger)
                progress >= 80 -> getColor(R.color.progress_warning)
                else -> getColor(R.color.progress_primary)
            }
            binding.usageProgress.progressBarColor = progressColor
            
            // Update today total
            binding.todayTotalTime.text = TimeUtils.formatDuration(todayTotal)
            
            // Update goal progress
            val goalProgress = if (dailyGoal > 0) {
                (todayTotal.toFloat() / (dailyGoal * 60 * 1000) * 100).coerceAtMost(100f)
            } else {
                0f
            }
            binding.goalProgressText.text = getString(R.string.goal_progress, goalProgress.toInt())
        }
    }
    
    private fun registerScreenReceiver() {
        screenStateReceiver = ScreenStateReceiver()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
        }
        registerReceiver(screenStateReceiver, filter)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        screenStateReceiver?.let {
            unregisterReceiver(it)
        }
        stopUpdatingUI()
    }
    
    override fun onResume() {
        super.onResume()
        if (UsageMonitorService.isRunning) {
            startUpdatingUI()
        }
        updateUI()
    }
    
    override fun onPause() {
        super.onPause()
        stopUpdatingUI()
    }
}