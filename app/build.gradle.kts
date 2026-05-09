import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.aiub.esportsclub"
    compileSdk = 36

    // ===== READ API KEY HERE — outside all blocks =====
    // This way BOTH defaultConfig and release can access it
    val geminiKey: String = run {
        val props = Properties()
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.reader().use { props.load(it) }
        }
        props.getProperty("GEMINI_API_KEY") ?: ""
    }

    defaultConfig {
        applicationId = "com.aiub.esportsclub"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // ===== CORE ANDROID =====
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")

    // ===== FIREBASE =====
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Firebase Cloud Messaging — for push notifications
    implementation("com.google.firebase:firebase-messaging-ktx")
    // Firebase Storage — for uploading images
    implementation("com.google.firebase:firebase-storage-ktx")

    // ===== GEMINI AI =====
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // ===== COROUTINES =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // ===== GLIDE =====
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ===== TESTING =====
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}