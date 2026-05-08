package com.aiub.esportsclub

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_IS_DARK = "theme_is_dark"

    // Save user's theme choice to SharedPreferences
    fun saveTheme(context: Context, isDark: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }

    // Read saved theme choice
    // Returns true = dark mode, false = light mode
    // Default is true (dark mode) if nothing saved yet
    fun isDarkMode(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_DARK, true)
    }

    // Apply the theme globally across the whole app
    fun applyTheme(isDark: Boolean) {
        if (isDark) {
            // MODE_NIGHT_YES forces dark mode
            // Android picks res/values-night/colors.xml automatically
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        } else {
            // MODE_NIGHT_NO forces light mode
            // Android picks res/values/colors.xml automatically
            AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
}