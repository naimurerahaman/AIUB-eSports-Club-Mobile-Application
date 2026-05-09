package com.aiub.esportsclub

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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        android.util.Log.d("PERMISSION", "Notification permission: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ===== DO NOT call enableEdgeToEdge() =====
        // We handle system bars manually via themes.xml
        // enableEdgeToEdge() causes content to draw behind bars
        // which is why we remove it here

        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        val isDark = ThemeManager.isDarkMode(this)
        ThemeManager.applyTheme(isDark)

        val layoutCenter = findViewById<LinearLayout>(R.id.layoutSplashCenter)
        val layoutBottom = findViewById<LinearLayout>(R.id.layoutSplashBottom)
        val tvLoading    = findViewById<TextView>(R.id.tvLoadingText)

        startAnimations(layoutCenter, layoutBottom)
        animateLoadingText(tvLoading)
        requestNotificationPermission()
        getFCMToken()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, SPLASH_DURATION)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                requestPermissionLauncher.launch(
                    android.Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                android.util.Log.d("FCM_TOKEN", "Token: $token")
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