package com.aiub.esportsclub

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Apply saved theme before any screen opens
        // This prevents the wrong theme flashing on startup
        val isDark = ThemeManager.isDarkMode(this)
        ThemeManager.applyTheme(isDark)
    }
}