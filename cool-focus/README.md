# 冷静 (Cool Focus) - 手机使用时长提醒App

## 项目概述

冷静是一款专注于数字健康的Android应用程序，旨在帮助用户监控和管理手机使用时间，培养健康的使用习惯，防止过度沉迷。

## 核心功能

### 🕐 实时监控
- 精确记录手机屏幕点亮时间，精确到秒
- 状态栏显示当前连续使用时长
- 应用内大字体可视化展示

### 🔔 智能提醒系统
- 自定义提醒阈值（默认30分钟）
- 多种提醒模式：单次、连续、持续提醒
- 智能休息判定：锁屏超过设定时间自动重置计时

### 📊 数据统计分析
- 今日/本周使用统计
- 休息次数、忽略提醒次数追踪
- 最高连续使用时长记录
- 每日目标设定与进度追踪

### ⚙️ 个性化设置
- 提醒阈值自定义
- 有效休息时长设置
- 通知方式选择（声音、震动、静音）
- 每日使用目标设定

## 技术架构

### 开发环境
- **语言**: Kotlin
- **最低SDK版本**: 21 (Android 5.0)
- **目标SDK版本**: 33 (Android 13)
- **构建工具**: Gradle 7.5

### 核心组件
- **前台服务**: UsageMonitorService - 持续监控使用时长
- **广播接收器**: ScreenStateReceiver - 监听屏幕状态变化
- **数据存储**: SharedPreferences - 存储用户设置和使用数据
- **图表库**: MPAndroidChart - 数据可视化
- **进度条**: CircularProgressBar - 圆形进度显示

### 权限需求
- 无障碍服务权限 - 精确监控应用使用
- 电池优化白名单 - 防止系统杀后台
- 悬浮窗权限 - 显示提醒弹窗
- 自启动权限 - 确保开机自启动

## 项目结构

```
app/src/main/java/com/coolfocus/app/
├── ui/                    # 用户界面
│   ├── MainActivity.kt   # 主界面
│   ├── SettingsActivity.kt # 设置界面
│   ├── StatisticsActivity.kt # 统计界面
│   ├── PermissionGuideActivity.kt # 权限引导
│   └── ReminderActivity.kt # 提醒弹窗
├── service/              # 后台服务
│   ├── UsageMonitorService.kt # 使用监控服务
│   └── ScreenStateService.kt # 屏幕状态服务
├── receiver/             # 广播接收器
│   ├── ScreenStateReceiver.kt # 屏幕状态接收器
│   └── BootReceiver.kt       # 开机启动接收器
├── utils/                # 工具类
│   ├── PreferencesManager.kt # 偏好设置管理
│   └── TimeUtils.kt        # 时间工具类
└── App.kt               # 应用主类
```

## 安装和运行

### 开发环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK 33
- Kotlin 1.7.21

### 构建项目
```bash
# 克隆项目后，在Android Studio中打开
# 或使用命令行构建
./gradlew build
```

### 安装APK
```bash
./gradlew installDebug
```

## 使用指南

1. **首次启动**: 按照权限引导页面授予必要权限
2. **设置参数**: 在设置页面调整提醒阈值、休息时长等参数
3. **开始监控**: 在主界面点击"开始监控"按钮
4. **查看统计**: 在统计页面查看使用数据和趋势

## 设计特色

### 🎨 极简设计
- 冷色调配色方案（科技蓝 #4A90E2，冷静紫 #8E44AD）
- 清晰的信息层次
- 直观的圆形进度指示器

### 🎯 用户体验
- 一键式操作
- 智能提醒不干扰
- 详细的数据反馈

### 🔒 隐私保护
- 本地数据存储，不上传用户信息
- 仅监控使用时长，不记录具体应用内容

## 版本历史

### v1.0.0 (2024-11-20)
- 基础功能实现
- 使用时长监控
- 智能提醒系统
- 数据统计分析

## 开发团队

- 项目名称：冷静 (Cool Focus)
- 版本：1.0.0
- 开发时间：2024年11月

## 许可证

本项目采用 Apache License 2.0 许可证。

---

**注意**: 这是一个演示项目，展示了基于提供的PRD文档的完整Android应用架构。在实际部署前，建议进行充分的测试和优化。