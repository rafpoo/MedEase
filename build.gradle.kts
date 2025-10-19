// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.android.library") version "8.5.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.2" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
}

//id("com.android.application") version "8.5.0" apply false
//id("com.android.library") version "8.5.0" apply false
//id("org.jetbrains.kotlin.android") version "1.9.23" apply false
//id("androidx.navigation.safeargs.kotlin") version "2.8.2" apply false