plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(21)

    jvm()

    androidLibrary {
        namespace = "com.bearminds.views"
        compileSdk = 36
        minSdk = 24
    }

    val xcfName = "viewsKit"
    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64()
    ).forEach {
        it.binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)

                // Architecture module (BaseViewModel, RootScreen, etc.)
                api(project(":architecture"))

                // Theme module
                implementation(project(":theme"))

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
