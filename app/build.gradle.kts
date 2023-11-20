plugins {
    id("com.android.application")
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.android")
    id("dagger.hilt.android.plugin")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
}

apply(from = "$rootDir/ktlint.gradle.kts")
apply(from = "$rootDir/secret.gradle.kts")

val secretDebug: List<Pair<String, String>> by project.extra
val stagingApi: List<Pair<String, String>> by project.extra
val releaseApi: List<Pair<String, String>> by project.extra
val openWeather: List<Pair<String, String>> by project.extra

kapt {
    correctErrorTypes = true
}
android {
    namespace = "com.egpayawal.openweather"
    signingConfigs {
        named("debug").configure {
            storeFile = file("$rootDir/app/${secretDebug[0].second}")
            storePassword = secretDebug[1].second

            keyAlias = secretDebug[3].second
            keyPassword = secretDebug[2].second
        }
    }

    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "com.egpayawal.openweather"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = getVersionCode()
        versionName = getVersionName()
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            releaseApi.forEach { buildConfigField("String", it.first, "\"${it.second}\"") }
            resValue("string", openWeather[0].first, openWeather[0].second)
        }

        debug {
            signingConfig = signingConfigs.getByName("debug")
            versionNameSuffix = "-dev"
            isDebuggable = true

            stagingApi.forEach { buildConfigField("String", it.first, "\"${it.second}\"") }
            resValue("string", openWeather[0].first, openWeather[0].second)
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

    // BP Module
    implementation(project(":data"))
    implementation(project(":common"))
    implementation(project(":commonStyles"))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.bom.analytics)

    // Glide
    implementation(libs.glide)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    kapt(libs.glide.compiler)

    // Arch Ext Compiler
    kapt(libs.androidx.lifecycle.compiler)

    // Lib Phone Number
    implementation(libs.lib.phone.number)

    // TouchImageView
    implementation(libs.touch.imageview)

    // Timber
    implementation(libs.timber)

    // Androidx
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Dagger-Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Shake Detector
    implementation(libs.shake.detector)

    implementation(libs.runtime.permission)
    implementation(libs.rx.kotlin)
    implementation(libs.google.play.services.location)
    implementation(libs.google.play.services.maps)
    implementation(libs.intuit.sdp)
    implementation(libs.androidx.swipe.refresher)

    // Testing
    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockk)
    testImplementation(testLibs.mockk.jvm)
    testImplementation(testLibs.google.truth)
    testImplementation(testLibs.androidx.arch.testing)
    testImplementation(testLibs.coroutines.test)

    androidTestImplementation(testLibs.junit)
    androidTestImplementation(testLibs.androidx.espresso)
    androidTestImplementation(testLibs.androidx.test.rules)
    androidTestImplementation(testLibs.androidx.test.runner)
    androidTestImplementation(testLibs.espresso.contrib)
    androidTestImplementation(testLibs.coroutines.test)
}

kapt {
    correctErrorTypes = true
}

fun getVersionCode(): Int {
    val major = "${project.properties["MAJOR"]}".toInt()
    val minor = "${project.properties["MINOR"]}".toInt()
    val patch = "${project.properties["PATCH"]}".toInt()
    val build = "${project.properties["BUILD"]}".toInt()
    return major * 10000 + minor * 1000 + patch * 10 + build
}

fun getVersionName(): String {
    val major = "${project.properties["MAJOR"]}"
    val minor = "${project.properties["MINOR"]}"
    val patch = "${project.properties["PATCH"]}"
    return "$major.$minor.$patch"
}
