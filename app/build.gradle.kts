plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cz.argetar.btdeviceswidget"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.argetar.btdeviceswidget"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
