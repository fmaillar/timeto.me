plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("app.cash.sqldelight").version("2.1.0")
}

kotlin {

    androidTarget()

    sourceSets {

        val ktor_version = "3.1.3"
        val sqldelight_version = "2.1.0"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                implementation("app.cash.sqldelight:primitive-adapters:$sqldelight_version")
                implementation("app.cash.sqldelight:coroutines-extensions:$sqldelight_version")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktor_version")
                implementation("app.cash.sqldelight:android-driver:$sqldelight_version")
            }
        }
    }
}

android {
    namespace = "me.timeto.shared"
    compileSdk = 35
    defaultConfig {
        minSdk = 26
    }
    compileOptions.sourceCompatibility = JavaVersion.VERSION_21
    compileOptions.targetCompatibility = JavaVersion.VERSION_21
}

sqldelight {
    databases {
        create("TimetomeDB") {
            packageName.set("me.timeto.appdbsq")
        }
    }
}
