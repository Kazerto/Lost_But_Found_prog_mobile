plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.myapplicationlbf"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplicationlbf"
        minSdk = 24
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

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Nouvelle ligne corrigée pour activer viewBinding
    viewBinding {
        enable = true
    }
}

dependencies {
    // Tu n'as pas besoin de déclarer à nouveau le plugin Gradle ici.
    // La ligne suivante est redondante et doit être supprimée :
    // implementation "com.android.tools.build:gradle:7.0.2"

    // Ajouter Retrofit pour les requêtes HTTP (récupération des API ou des requêtes du backend pour envoyer les données dans la DB).
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    implementation ("com.github.bumptech.glide:glide:4.13.0")
    implementation(libs.play.services.location)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.13.0")

    implementation ("com.google.android.gms:play-services-location:18.3.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    // AndroidX Libraries via `libs` (si tu utilises `libs.versions.toml`)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Test dependencies via `libs` (si tu utilises `libs.versions.toml`)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
