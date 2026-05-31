package net.grongubbe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class LwjglNativesPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(
            "lwjglNatives",
            LwjglNativesExtension::class.java,
            detectLwjglNatives()
        )
    }

    private fun detectLwjglNatives(): String {
        val os = System.getProperty("os.name").lowercase()
        val arch = System.getProperty("os.arch").lowercase()

        return when {
            os.contains("win") -> when {
                arch.contains("aarch64") || arch.contains("arm64") -> "natives-windows-arm64"
                arch.contains("64") -> "natives-windows"
                else -> "natives-windows-x86"
            }

            os.contains("mac") || os.contains("darwin") -> when {
                arch.contains("aarch64") || arch.contains("arm64") -> "natives-macos-arm64"
                else -> "natives-macos"
            }

            os.contains("linux") || os.contains("nux") || os.contains("nix") || os.contains("aix") -> when {
                arch.contains("aarch64") || arch.contains("arm64") -> "natives-linux-arm64"
                arch.startsWith("arm") -> "natives-linux-arm32"
                arch.contains("ppc64le") -> "natives-linux-ppc64le"
                arch.contains("riscv64") -> "natives-linux-riscv64"
                else -> "natives-linux"
            }

            os.contains("freebsd") -> "natives-freebsd"

            else -> error("Unsupported platform: $os / $arch")
        }
    }
}