import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Nilai di local.properties dibaca lewat providers.fileContents supaya configuration
// cache ikut invalid saat API key diganti.
val localProperties = providers.fileContents(
    rootProject.layout.projectDirectory.file("local.properties")
).asText.orElse("").map { text ->
    Properties().apply { load(text.reader()) }
}

fun localProperty(name: String, fallback: String = ""): String =
    localProperties.get().getProperty(name, fallback)

val booksApiKey = localProperty("BOOKS_API_KEY")
val booksBaseUrl = localProperty("BOOKS_BASE_URL", "https://www.googleapis.com/books/v1/")

if (booksApiKey.isBlank()) {
    logger.warn("BOOKS_API_KEY belum diisi di local.properties; aplikasi akan menampilkan pesan API key kosong.")
}

android {
    namespace = "dev.atalariq.gobooks"
    compileSdk = 37
    compileSdkMinor = 1

    defaultConfig {
        applicationId = "dev.atalariq.gobooks"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "API_KEY", "\"$booksApiKey\"")
        buildConfigField("String", "BASE_URL", "\"$booksBaseUrl\"")
    }

    buildTypes {
        release {
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.coil.compose)
}
