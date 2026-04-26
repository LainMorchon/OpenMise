import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    //alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

// 1. Leer local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val proxyUrl = localProperties.getProperty("PROXY_URL") ?: ""

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm() // Desktop

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        
        val commonMain by getting {
            // 2. Registrar la generación del archivo de configuración (Cache-friendly)
            val generateFatSecretConfig = tasks.register("generateFatSecretConfig") {
                val url = proxyUrl
                val outputDir = layout.buildDirectory.dir("generated/fatsecret/commonMain/kotlin")
                
                // Declaramos entradas y salidas para que Gradle gestione el caché correctamente
                inputs.property("proxyUrl", url)
                outputs.dir(outputDir)

                doLast {
                    val baseDir = outputDir.get().asFile
                    val configFile = baseDir.resolve("com/morchon/lain/core/config/FatSecretConfig.kt")
                    configFile.parentFile.mkdirs()
                    configFile.writeText(
                        """
                        package com.morchon.lain.core.config

                        /**
                         * ARCHIVO GENERADO AUTOMÁTICAMENTE. NO EDITAR NI SUBIR AL REPOSITORIO.
                         */
                        object FatSecretConfig {
                            const val BASE_URL = "$url"
                        }
                        """.trimIndent()
                    )
                }
            }
            // Añadir el directorio generado a las fuentes de commonMain
            kotlin.srcDir(generateFatSecretConfig)

            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)

                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
                implementation(libs.androidx.navigation.compose)
                implementation(compose.materialIconsExtended)

                // Koin
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // Room & SQLite
                implementation(libs.androidx.room.runtime)
                implementation(libs.sqlite.bundled)

                // Paging
                implementation(libs.paging.common)
                implementation(libs.paging.compose.common)

                // Utils
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)

                // Coil KMP
                implementation(libs.coil.compose)
                implementation(libs.coil.network.ktor)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
                implementation(libs.ktor.client.okhttp)
            }
        }

        // Añadimos el motor para Android
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
    }
}

android {
    namespace = "com.morchon.lain"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.morchon.lain"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspJvm", libs.androidx.room.compiler)
}

compose.desktop {
    application {
        mainClass = "com.morchon.lain.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.morchon.lain"
            packageVersion = "1.0.0"
        }
    }
}
