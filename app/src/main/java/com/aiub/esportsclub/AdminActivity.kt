package com.aiub.esportsclub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        // ===== FIX STATUS BAR OVERLAY =====
// This tells Android: "don't let your app draw behind system bars"
        WindowCompat.setDecorFitsSystemWindows(window, true)

// Set status bar color to match toolbar
        window.statusBarColor = android.graphics.Color.parseColor("#1A1A2E")

// Set navigation bar color to match app background
        window.navigationBarColor = android.graphics.Color.parseColor("#0D0D0D")

// Make status bar icons WHITE (visible on dark background)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars    = false  // false = white icons
            isAppearanceLightNavigationBars = false  // false = white icons
        }
        // Load AdminDashboardFragment as the first admin screen
        if (savedInstanceState == null) {
            loadFragment(AdminDashboardFragment(), addToBackStack = false)
        }
    }

    // Same loadFragment function but uses adminFragmentContainer
    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.adminFragmentContainer, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}