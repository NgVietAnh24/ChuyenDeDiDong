plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "vn.vietanhnguyen.khachhangdatmon"
    compileSdk = 34  // Tối ưu hóa cho Android 14

    defaultConfig {
        applicationId = "vn.vietanhnguyen.khachhangdatmon"
        minSdk = 30  // Hỗ trợ từ Android 11 (API 30) trở lên
        targetSdk = 34  // Android 14
        versionCode = 1
        versionName = "1.1"

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

    implementation(fileTree(mapOf(
        "dir" to "C:\\Users\\ADMIN\\OneDrive\\Desktop\\ZaloPayLibs",
        "include" to listOf("*.aar", "*.jar"),
        "exclude" to listOf("")
    )))
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-database:19.6.0")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth:21.0.1")
    implementation ("com.google.android.gms:play-services-auth:19.2.0")
    implementation("com.google.firebase:firebase-firestore")
    implementation ("com.airbnb.android:lottie:6.1.0")
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation ("com.google.android.material:material:1.4.0")
    implementation ("com.google.code.gson:gson:2.8.8")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}