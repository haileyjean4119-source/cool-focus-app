package com.coolfocus.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coolfocus.app.R
import com.coolfocus.app.databinding.ActivityStatisticsBinding
import com.coolfocus.app.service.UsageMonitorService
import com.coolfocus.app.utils.PreferencesManager
import com.coolfocus.app.utils.TimeUtils
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class StatisticsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var preferencesManager: PreferencesManager
    private var isShowingToday = true
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferencesManager = PreferencesManager(this)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.statistics_title)
        
        setupUI()
        updateStatistics()
    }
    
    private fun setupUI() {
        binding.todayButton.setOnClickListener {
            isShowingToday = true
            binding.todayButton.isSelected = true
            binding.weekButton.isSelected = false
            updateStatistics()
        }
        
        binding.weekButton.setOnClickListener {
            isShowingToday = false
            binding.weekButton.isSelected = true
            binding.todayButton.isSelected = false
            updateStatistics()
        }
        
        // Initialize chart
        setupChart(binding.usageChart)
    }
    
    private fun setupChart(chart: BarChart) {
        chart.description.isEnabled = false
        chart.setDrawGridBackground(false)
        chart.setDrawBarShadow(false)
        chart.setDrawValueAboveBar(true)
        
        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        
        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        
        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        
        chart.legend.isEnabled = false
    }
    
    private fun updateStatistics() {
        if (isShowingToday) {
            updateTodayStatistics()
        } else {
            updateWeekStatistics()
        }
    }
    
    private fun updateTodayStatistics() {
        // Get today's data
        val todayTotal = UsageMonitorService.todayTotalTime
        val breakCount = preferencesManager.getBreakCount()
        val ignoreCount = preferencesManager.getIgnoreCount()
        val maxContinuousUsage = preferencesManager.getMaxContinuousUsage()
        val dailyGoal = preferencesManager.getDailyGoal()
        
        // Update UI
        binding.totalUsageTime.text = TimeUtils.formatDuration(todayTotal)
        binding.breakCount.text = breakCount.toString()
        binding.ignoreCount.text = ignoreCount.toString()
        binding.maxContinuousUsage.text = TimeUtils.formatDurationShort(maxContinuousUsage)
        
        // Update goal progress
        val goalProgress = if (dailyGoal > 0) {
            (todayTotal.toFloat() / (dailyGoal * 60 * 1000) * 100).coerceAtMost(100f)
        } else {
            0f
        }
        binding.goalProgressCircle.progress = goalProgress
        binding.goalProgressText.text = getString(R.string.goal_progress, goalProgress.toInt())
        
        // Update chart with sample data (you would replace this with real data)
        updateChartWithSampleData()
    }
    
    private fun updateWeekStatistics() {
        // This would show weekly aggregated data
        // For now, showing the same data as today
        updateTodayStatistics()
    }
    
    private fun updateChartWithSampleData() {
        val entries = mutableListOf<BarEntry>()
        
        // Sample data for the last 7 days
        for (i in 0..6) {
            val hours = (1..8).random().toFloat()
            entries.add(BarEntry(i.toFloat(), hours))
        }
        
        val dataSet = BarDataSet(entries, "使用时长")
        dataSet.color = getColor(R.color.chart_blue)
        dataSet.valueTextColor = getColor(R.color.text_primary)
        
        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
        
        binding.usageChart.data = barData
        
        // Customize X-axis
        val days = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        binding.usageChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return days.getOrNull(value.toInt()) ?: ""
            }
        }
        
        binding.usageChart.invalidate()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}