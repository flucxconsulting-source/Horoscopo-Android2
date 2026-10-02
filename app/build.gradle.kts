plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.flucx.horoscopoandroid2"
    compileSdk {
        version = release(37)
    }

    val divineApiKey = providers.gradleProperty("DIVINE_API_KEY").orElse("").get()
    val divineAuthToken = providers.gradleProperty("DIVINE_AUTH_TOKEN").orElse("").get()

    defaultConfig {
        applicationId = "com.flucx.horoscopoandroid2"
        minSdk = 30
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "DIVINE_API_KEY", "\"$divineApiKey\"")
        buildConfigField("String", "DIVINE_AUTH_TOKEN", "\"$divineAuthToken\"")
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)
    implementation(libs.moshi.kotlin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
