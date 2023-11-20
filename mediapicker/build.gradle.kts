plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}
apply(from = "$rootDir/ktlint.gradle.kts")

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.egpayawal.mediapicker"
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        jvmTarget = "1.8"
    }
}

dependencies {
    // Androidx
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.fragment)

    // Zelory Compressor
    implementation(libs.zelory.compressor)
    implementation(libs.light.compressor)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("Maven") {
                from(components["release"])
                groupId = project.properties["ARTIFACT_PACKAGE"].toString()
                artifactId = project.properties["ARTIFACT_NAME"].toString()
                version = "${project.properties["major"]}.${project.properties["minor"]}.${project.properties["patch"]}"
            }
        }

        repositories {
            maven {
                url = uri("https://gitlab.com/api/v4/projects/43665670/packages/maven")
                credentials(HttpHeaderCredentials::class) {
                    name = project.properties["privateTokenName"].toString()
                    value = project.properties["personalAccessToken"].toString()
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }
    }
}
