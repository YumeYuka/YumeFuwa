plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    js(IR) {
        outputModuleName = "yumefuwa"
        browser {
            commonWebpackConfig {
                outputFileName = "yumefuwa.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.9.3")
            implementation("org.jetbrains.compose.foundation:foundation:1.9.3")
            implementation("org.jetbrains.compose.ui:ui:1.9.3")
            implementation("top.yukonga.miuix.kmp:miuix-js:0.7.2")
            implementation("io.github.panpf.sketch4:sketch-compose:4.4.0-alpha01")
            implementation("io.github.panpf.sketch4:sketch-http:4.4.0-alpha01")
            implementation("io.github.panpf.sketch4:sketch-animated-webp:4.4.0-alpha01")
        }
        val jsMain by getting {
            dependencies {
                // JS specific dependencies can be added here if needed
            }
        }
    }
}
