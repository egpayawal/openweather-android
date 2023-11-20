plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

apply(from = "$rootDir/secret.gradle.kts")
apply(from = "$rootDir/ktlint.gradle.kts")

val stagingApi: List<Pair<String, String>> by project.extra
val releaseApi: List<Pair<String, String>> by project.extra

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.egpayawal.module.network"
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            releaseApi.forEach { buildConfigField("String", "${it.first}", "\"${it.second}\"") }
        }

        debug {
            stagingApi.forEach { buildConfigField("String", "${it.first}", "\"${it.second}\"") }
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

    // Gson
    api(libs.gson)

    // Retrofit
    api(libs.retrofit)
    api(libs.retrofit.gson)
    api(libs.retrofit.scalars)

    // OkHttp
    api(libs.okhttp)
    api(libs.okhttp.logging)

    // Androidx
    api(libs.androidx.paging.runtime)

    // Dagger-Hilt
    kapt(libs.hilt.compiler)
    api(libs.hilt.android)

    // Chucker
    debugApi(libs.chucker)
    releaseApi(libs.chucker.noop)

    // Testing
    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.jvm)
    testImplementation(testLibs.google.truth)
    testImplementation(testLibs.coroutines.test)
    androidTestImplementation(testLibs.androidx.test.runner)
    androidTestImplementation(testLibs.androidx.espresso)
    androidTestImplementation(testLibs.coroutines.test)
}

kapt {
    correctErrorTypes = true
}
