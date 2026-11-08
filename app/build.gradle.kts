plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.vic.devocional"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.vic.devocional"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.exifinterface:exifinterface:1.4.2")
    testImplementation("junit:junit:4.13.2")
}


