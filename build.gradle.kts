import org.jetbrains.kotlin.utils.addToStdlib.safeAs

// Top-level build file where you can add configuration options common to all sub-projects/modules.
apply(from = "secret.gradle.kts")

plugins {
    alias(libs.plugins.android) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.navigation) apply false
    alias(libs.plugins.hilt) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("installGitHook", Copy::class) {
    from("${rootProject.rootDir}/pre-commit")
    into("${rootProject.rootDir}/.git/hooks")
    fileMode = 777
}

tasks.getByPath(":app:preBuild").dependsOn(tasks.getByName("installGitHook"))
