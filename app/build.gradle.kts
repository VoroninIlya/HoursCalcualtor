plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.svilvo.hourscalculator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.svilvo.hourscalculator"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.room.common)
    implementation(libs.room.runtime)
    implementation(project(":hc_database"))
    implementation("io.noties.markwon:core:4.6.2")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("androidx.startup:startup-runtime:1.2.0")
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    annotationProcessor(libs.room.compiler)

}