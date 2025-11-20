package com.coolfocus.app.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.coolfocus.app.R
import com.coolfocus.app.databinding.ActivityPermissionGuideBinding

class PermissionGuideActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPermissionGuideBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.permission_guide_title)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.accessibilityPermission.setOnClickListener {
            openAccessibilitySettings()
        }
        
        binding.batteryPermission.setOnClickListener {
            openBatteryOptimizationSettings()
        }
        
        binding.overlayPermission.setOnClickListener {
            requestOverlayPermission()
        }
        
        binding.autostartPermission.setOnClickListener {
            openAutostartSettings()
        }
    }
    
    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }
    
    private fun openBatteryOptimizationSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            startActivity(intent)
        }
    }
    
    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName"))
            startActivity(intent)
        }
    }
    
    private fun openAutostartSettings() {
        // This is manufacturer-specific
        try {
            val intent = Intent()
            when (android.os.Build.MANUFACTURER.toLowerCase()) {
                "xiaomi" -> {
                    intent.component = android.content.ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }
                "huawei" -> {
                    intent.component = android.content.ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                }
                "oppo" -> {
                    intent.component = android.content.ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }
                "vivo" -> {
                    intent.component = android.content.ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }
                else -> {
                    // Fallback to general settings
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.parse("package:$packageName")
                }
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general settings
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}