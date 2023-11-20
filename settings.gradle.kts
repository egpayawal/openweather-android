pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.google.com/") }
        jcenter()
    }

    versionCatalogs {
        create("libs") {
            from(files("gradle-dependencies.toml"))
        }
        create("testLibs") {
            from(files("testing-dependencies.toml"))
        }
    }
}
include(":app")
include(":domain")
include(":local")
include(":network")
include(":data")
include(":common")
include(":commonStyles")
include(":mediapicker")
