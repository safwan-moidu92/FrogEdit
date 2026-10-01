import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "com.osm.frogedit.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.osm.frogedit"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("icons/frog_edit.png"))
            }
            windows {
                iconFile.set(project.file("icons/frog_edit.ico"))
            }
            macOS {
                iconFile.set(project.file("icons/frog_edit.icns"))
            }
        }
    }
}