plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.androidTest) apply false
    alias(libs.plugins.androidxBaselineprofile) apply false
    alias(libs.plugins.dependencyGraphGenerator) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.sqlDelight) apply false
}

buildscript {
    dependencies {
        classpath(libs.google.ossLicensesPlugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
