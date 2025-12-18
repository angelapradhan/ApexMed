plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.doctors"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.doctors"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.firebase.database)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Core KTXplugins {
    //    alias(libs.plugins.android.application)
    //    alias(libs.plugins.kotlin.android)
    //    alias(libs.plugins.kotlin.compose)
    //}
    //
    //android {
    //    namespace = "com.example.doctors"
    //    compileSdk {
    //        version = release(36)
    //    }
    //
    //    defaultConfig {
    //        applicationId = "com.example.doctors"
    //        minSdk = 24
    //        targetSdk = 36
    //        versionCode = 1
    //        versionName = "1.0"
    //
    //        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    //    }
    //
    //    buildTypes {
    //        release {
    //            isMinifyEnabled = false
    //            proguardFiles(
    //                getDefaultProguardFile("proguard-android-optimize.txt"),
    //                "proguard-rules.pro"
    //            )
    //        }
    //    }
    //    compileOptions {
    //        sourceCompatibility = JavaVersion.VERSION_11
    //        targetCompatibility = JavaVersion.VERSION_11
    //    }
    //    kotlinOptions {
    //        jvmTarget = "11"
    //    }
    //    buildFeatures {
    //        compose = true
    //    }
    //}
    //
    // Core KTX
    implementation("androidx.core:core-ktx:1.12.0")

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.foundation:foundation")

    // ADD THIS CRUCIAL LINE FOR ICONS
    implementation("androidx.compose.material:material-icons-extended:")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // Coil for image loading (Optional, but good practice)
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation(libs.androidx.material3)

    // Firebase Authentication
    //implementation("com.google.firebase:firebase-auth-ktx")

    // Firebase Realtime Database
    //implementation("com.google.firebase:firebase-database-ktx")

    // Platform (BOM)
    //implementation(platform("com.google.firebase:firebase-bom:32.x.x"))
}