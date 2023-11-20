plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
}
apply(from = "$rootDir/ktlint.gradle.kts")
apply(from = "$rootDir/secret.gradle.kts")

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.egpayawal.module.data"
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(project(":network"))
    api(project(":local"))
    api(project(":domain"))
//    implementation(project(":authCore"))

    // ViewModel & LiveData
    api(libs.androidx.viewmodel)
    implementation(libs.google.play.services.auth)
    kapt(libs.androidx.lifecycle.compiler)

    kapt(libs.hilt.compiler)

    // GlassFish
    compileOnly(libs.glassfish)

    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.jvm)
    testImplementation(testLibs.google.truth)
    testImplementation(testLibs.coroutines.test)
}

kapt {
    correctErrorTypes = true
}
