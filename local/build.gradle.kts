plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}
apply(from = "$rootDir/ktlint.gradle.kts")

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.egpayawal.module.local"
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":domain"))

    api(libs.room.runtime)
    api(libs.room.ktx)
    kapt(libs.room.compiler)

    api(libs.androidx.preference)

    implementation(libs.sql.cipher)
    implementation(libs.sql.lite)

    // EncryptedSharedPreferences
    implementation(libs.security.crypto)

    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.jvm)
    testImplementation(testLibs.google.truth)
    testImplementation(testLibs.coroutines.test)
    testImplementation(testLibs.androidx.test.runner)
    androidTestImplementation(testLibs.androidx.test.runner)
    androidTestImplementation(testLibs.androidx.espresso)
}

kapt {
    correctErrorTypes = true
}
