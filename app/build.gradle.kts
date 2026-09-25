plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.bia.sky_runner"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bia.skyrunner"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
}
