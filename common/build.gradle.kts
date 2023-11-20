plugins {
    id("maven-publish")
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

apply(from = "$rootDir/ktlint.gradle.kts")

android {
    namespace = "com.egpayawal.baseplate.common"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // BP Modules
    implementation(project(":mediapicker"))

    // Kotlin Std
    implementation(libs.kotlinStdLib)

    // Androidx
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.support.design)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.paging.runtime)

    // Dagger-Hilt
    implementation(libs.hilt.android)

    // Arch Lifecycle
    implementation(libs.androidx.lifecycle.common)

    // Timber
    implementation(libs.timber)

    // Circle Image View
    implementation(libs.circle.imageview)

    // Time Ago
    implementation(libs.timeago)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment)

    // Glide
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Arch Ext
    kapt(libs.androidx.lifecycle.compiler)

    // Facebook Shimmer
    implementation(libs.facebook.shimmer)

    // Permission
    implementation(libs.runtime.permission)
    implementation(libs.permission.dispatcher)

    // Flow
    implementation(libs.flowbinding.android)
    implementation(libs.flowbinding.core)
    implementation(libs.flowbinding.appcompat)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // UCrop
    implementation(libs.ucrop)

    // Data Binding
    kapt(libs.databinding.compiler)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.arch.core.testing)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.androidx.test.runner)
}

kapt {
    correctErrorTypes = true
}
