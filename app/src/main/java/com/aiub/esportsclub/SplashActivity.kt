package com.aiub.esportsclub

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val SPLASH_DURATION = 2500L

    // ===== NOTIFICATION PERMISSION LAUNCHER =====
    // This is the modern way to request permissions in Android
    // registerForActivityResult handles the user's yes/no response
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // isGranted = true  → user allowed notifications
        // isGranted = false → user denied notifications
        // Either way we continue the app — notifications are optional
        android.util.Log.d("PERMISSION", "Notification permission: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        val isDark = ThemeManager.isDarkMode(this)
        ThemeManager.applyTheme(isDark)

        val layoutCenter = findViewById<LinearLayout>(R.id.layoutSplashCenter)
        val layoutBottom = findViewById<LinearLayout>(R.id.layoutSplashBottom)
        val tvLoading    = findViewById<TextView>(R.id.tvLoadingText)

        // Start animations
        startAnimations(layoutCenter, layoutBottom)
        animateLoadingText(tvLoading)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        // Get and log FCM token (useful for testing)
        getFCMToken()

        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, SPLASH_DURATION)
    }

    // ===== REQUEST NOTIFICATION PERMISSION =====
    private fun requestNotificationPermission() {
        // POST_NOTIFICATIONS permission only needed on Android 13 (API 33)+
        // Older Android versions don't need this permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            // Check if permission is already granted
            val isGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!isGranted) {
                // Ask the user for permission
                // A system dialog will appear asking "Allow notifications?"
                requestPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
        // Below Android 13 → notifications work without asking
    }

    // ===== GET FCM TOKEN =====
    // The FCM token is like the phone number of this specific app installation
    // Firebase sends notifications TO this token
    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                // Log the token so you can copy it for testing
                android.util.Log.d("FCM_TOKEN", "Token: $token")
            }
            .addOnFailureListener { exception ->
                android.util.Log.e("FCM_TOKEN", "Failed: ${exception.message}")
            }
    }

    private fun startAnimations(
        layoutCenter: LinearLayout,
        layoutBottom: LinearLayout
    ) {
        val centerAlpha  = ObjectAnimator.ofFloat(layoutCenter, "alpha",  0f, 1f)
        val centerScaleX = ObjectAnimator.ofFloat(layoutCenter, "scaleX", 0.5f, 1f)
        val centerScaleY = ObjectAnimator.ofFloat(layoutCenter, "scaleY", 0.5f, 1f)

        val centerSet = AnimatorSet()
        centerSet.playTogether(centerAlpha, centerScaleX, centerScaleY)
        centerSet.duration     = 800
        centerSet.interpolator = AccelerateDecelerateInterpolator()
        centerSet.start()

        val bottomAlpha = ObjectAnimator.ofFloat(layoutBottom, "alpha", 0f, 1f)
        bottomAlpha.duration   = 600
        bottomAlpha.startDelay = 600
        bottomAlpha.start()
    }

    private fun animateLoadingText(tvLoading: TextView) {
        val handler  = Handler(Looper.getMainLooper())
        val messages = listOf(
            "Loading...",
            "Checking authentication...",
            "Almost ready..."
        )
        var index = 0
        val runnable = object : Runnable {
            override fun run() {
                if (index < messages.size) {
                    tvLoading.text = messages[index]
                    index++
                    handler.postDelayed(this, 700)
                }
            }
        }
        handler.postDelayed(runnable, 600)
    }

    private fun navigateToNextScreen() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }
}