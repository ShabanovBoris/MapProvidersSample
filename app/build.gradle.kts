import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "ru.bosha.mapssample"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.bosha.mapssample"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties().apply {
            load(rootProject.file("./local.properties").reader())
        }

        val googleMapsKey = properties["GOOGLE_MAPS_API_KEY"] as String
        val mapkitKey = properties["MAPKIT_API_KEY"] as String

        manifestPlaceholders["GOOGLE_MAPS_API_KEY"] = googleMapsKey
        buildConfigField("String", "MAPKIT_API_KEY", mapkitKey)
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)

    //Maps
    implementation(libs.maps.ktx)
    implementation(libs.play.services.maps)
    implementation(libs.maps.mobile)
}