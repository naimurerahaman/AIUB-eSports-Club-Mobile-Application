package com.aiub.esportsclub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// FirebaseMessagingService is a special Service class from Firebase
// It runs in the background and listens for incoming notifications
// We extend it (inherit from it) to add our own behavior
class MyFirebaseMessagingService : FirebaseMessagingService() {

    // ===== CHANNEL CONSTANTS =====
    // A Notification Channel is like a category for notifications
    // Android 8.0+ requires channels — without them, no notifications show
    companion object {
        const val CHANNEL_ID   = "aiub_esports_channel"
        const val CHANNEL_NAME = "AIUB eSports Club"
        const val CHANNEL_DESC = "Notifications for AIUB eSports Club events and updates"
    }

    // ===== onMessageReceived =====
    // This function is called automatically by Firebase
    // whenever a new notification arrives on this device
    // "remoteMessage" contains the notification data (title, body, etc.)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // remoteMessage can contain two types of data:
        // 1. notification payload — has title and body (easy)
        // 2. data payload — custom key-value pairs (flexible)

        // Check if notification has a title and body
        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "AIUB eSports Club"
        // ?: means "if null, use this instead"
        // We first try notification.title, then data["title"], then default

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "You have a new notification"

        // Show the notification on the device
        showNotification(title, body)
    }

    // ===== onNewToken =====
    // Called when Firebase generates a NEW token for this device
    // A token is a unique ID for this specific app installation
    // Firebase uses it to know which device to send the notification to
    // Think of it like a postal address for your app
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // Log the token so we can see it in Logcat
        android.util.Log.d("FCM_TOKEN", "New token: $token")

        // In a real app, you would save this token to Firestore
        // so the server knows where to send notifications
        // For this project, Firebase Console handles it automatically
        saveTokenToFirestore(token)
    }

    // ===== SAVE TOKEN TO FIRESTORE =====
    // We save the FCM token linked to the user's account
    // This is used for sending notifications to specific users
    private fun saveTokenToFirestore(token: String) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val db   = com.google.firebase.firestore.FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid ?: return
        // If user is not logged in, we skip saving the token

        // Save token to the user's profile document in Firestore
        db.collection("users")
            .document(userId)
            .update("fcmToken", token)
            .addOnFailureListener {
                // If document doesn't exist yet, create it with set()
                db.collection("users")
                    .document(userId)
                    .set(mapOf("fcmToken" to token))
            }
    }

    // ===== SHOW NOTIFICATION =====
    // This function creates and displays the notification
    // on the device's notification tray
    private fun showNotification(title: String, body: String) {

        // ===== STEP 1: CREATE NOTIFICATION CHANNEL =====
        // Required for Android 8.0 (API 26) and above
        // Think of it as creating a category/folder for your notifications
        createNotificationChannel()

        // ===== STEP 2: CREATE PENDING INTENT =====
        // PendingIntent decides what happens when user TAPS the notification
        // We want it to open SplashActivity (which handles login check)
        val intent = Intent(this, SplashActivity::class.java).apply {
            // These flags ensure the app opens cleanly
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // PendingIntent wraps our Intent and gives it to the system
        // The system fires it when user taps the notification
        // FLAG_IMMUTABLE = the PendingIntent cannot be changed
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // ===== STEP 3: BUILD THE NOTIFICATION =====
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            // Small icon shown in status bar — use your app icon
            .setSmallIcon(R.mipmap.ic_launcher)
            // Title of the notification (bold text at top)
            .setContentTitle(title)
            // Body text below the title
            .setContentText(body)
            // Priority — HIGH means it shows as a popup (heads-up notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // Auto cancel — notification disappears when user taps it
            .setAutoCancel(true)
            // What happens when user taps the notification
            .setContentIntent(pendingIntent)
            // Show full text if body is long
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            // Vibrate and make sound
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        // ===== STEP 4: SHOW THE NOTIFICATION =====
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        // notify() shows the notification
        // First parameter is a unique ID — use different IDs for different notifications
        // We use current time as ID so each notification is unique
        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    // ===== CREATE NOTIFICATION CHANNEL =====
    // Required for Android 8.0+
    // Only needs to be created once — Android ignores duplicates
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // IMPORTANCE_HIGH = shows as heads-up popup notification
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                // Enable vibration
                enableVibration(true)
                // Enable lights (LED notification light)
                enableLights(true)
            }

            val notificationManager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            // Create the channel
            notificationManager.createNotificationChannel(channel)
        }
    }
}