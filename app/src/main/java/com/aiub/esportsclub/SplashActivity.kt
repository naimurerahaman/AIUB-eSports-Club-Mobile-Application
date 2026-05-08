package com.aiub.esportsclub

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    // FirebaseAuth helps us check if user is already logged in
    private lateinit var auth: FirebaseAuth

    // SPLASH_DURATION = how long to show the splash screen
    // 2500 milliseconds = 2.5 seconds
    private val SPLASH_DURATION = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Connect to Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Apply the saved theme (dark/light)
        val isDark = ThemeManager.isDarkMode(this)
        ThemeManager.applyTheme(isDark)

        // Find the layout views for animation
        val layoutCenter = findViewById<LinearLayout>(R.id.layoutSplashCenter)
        val layoutBottom = findViewById<LinearLayout>(R.id.layoutSplashBottom)
        val tvLoading    = findViewById<TextView>(R.id.tvLoadingText)

        // ===== START ANIMATIONS =====
        startAnimations(layoutCenter, layoutBottom)

        // ===== LOADING TEXT ANIMATION =====
        // Changes the loading text every 800ms to look like it's working
        animateLoadingText(tvLoading)

        // ===== TIMER — NAVIGATE AFTER DELAY =====
        // Handler runs code on the main thread
        // postDelayed runs the code AFTER the specified delay
        //
        // Think of it like setting an alarm:
        // "After 2.5 seconds, run this code"
        Handler(Looper.getMainLooper()).postDelayed({
            // This code runs after SPLASH_DURATION milliseconds
            navigateToNextScreen()
        }, SPLASH_DURATION)
    }

    // ===== ANIMATION FUNCTION =====
    private fun startAnimations(
        layoutCenter: LinearLayout,
        layoutBottom: LinearLayout
    ) {
        // ===== CENTER LOGO ANIMATION =====
        // We animate 3 things together:
        // 1. Alpha (opacity): 0 → 1 (invisible to visible)
        // 2. ScaleX: 0.5 → 1.0 (small to normal size)
        // 3. ScaleY: 0.5 → 1.0 (small to normal size)

        // Alpha animation — fade in
        val centerAlpha = ObjectAnimator.ofFloat(
            layoutCenter,  // which view to animate
            "alpha",       // which property to change
            0f, 1f         // from 0 (invisible) to 1 (fully visible)
        )

        // Scale X animation — grow from small to normal
        val centerScaleX = ObjectAnimator.ofFloat(
            layoutCenter,
            "scaleX",
            0.5f, 1f   // from 50% size to 100% size
        )

        // Scale Y animation — grow from small to normal
        val centerScaleY = ObjectAnimator.ofFloat(
            layoutCenter,
            "scaleY",
            0.5f, 1f
        )

        // AnimatorSet plays multiple animations at the same time
        val centerSet = AnimatorSet()
        centerSet.playTogether(centerAlpha, centerScaleX, centerScaleY)
        centerSet.duration = 800  // animation takes 800ms
        // AccelerateDecelerateInterpolator makes animation start slow,
        // speed up in middle, then slow down at end — feels natural
        centerSet.interpolator = AccelerateDecelerateInterpolator()
        centerSet.start()

        // ===== BOTTOM LOADING ANIMATION =====
        // Fade in after a small delay (400ms) so it appears after the logo
        val bottomAlpha = ObjectAnimator.ofFloat(layoutBottom, "alpha", 0f, 1f)
        bottomAlpha.duration    = 600
        bottomAlpha.startDelay  = 600  // wait 600ms before starting
        bottomAlpha.start()
    }

    // ===== LOADING TEXT ANIMATION =====
    // Changes text to make it look like the app is doing something
    private fun animateLoadingText(tvLoading: TextView) {
        val handler  = Handler(Looper.getMainLooper())
        val messages = listOf(
            "Loading...",
            "Checking authentication...",
            "Almost ready..."
        )
        var index = 0

        // This Runnable runs repeatedly every 700ms
        val runnable = object : Runnable {
            override fun run() {
                if (index < messages.size) {
                    tvLoading.text = messages[index]
                    index++
                    // Schedule itself to run again after 700ms
                    handler.postDelayed(this, 700)
                }
            }
        }
        // Start the first run after 600ms
        handler.postDelayed(runnable, 600)
    }

    // ===== NAVIGATE TO CORRECT SCREEN =====
    private fun navigateToNextScreen() {

        // auth.currentUser returns the logged-in user
        // If it's NOT null → user is already logged in
        // If it IS null    → user is not logged in
        val currentUser = auth.currentUser

        if (currentUser != null) {
            // ✅ User is already logged in
            // Go directly to the Home Screen (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // ❌ User is not logged in
            // Go to Login Screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // finish() closes the SplashActivity
        // This is VERY important — without it, pressing Back
        // from the next screen would return to the splash screen
        finish()
    }
}