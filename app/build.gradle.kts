plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "vn.vietanhnguyen.khachhangdatmon"
    compileSdk = 34

    defaultConfig {
        applicationId = "vn.vietanhnguyen.khachhangdatmon"
        minSdk = 33
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
// Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.airbnb.android:lottie:6.1.0")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}