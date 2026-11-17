import org.gradle.api.file.DuplicatesStrategy

plugins {
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("net.grongubbe.tengoku.client.ClientMain")
}

dependencies {
    implementation(project(":common"))
    implementation(platform(libs.lwjgl.bom))
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.glfw)
    implementation(libs.lwjgl.opengl)
    implementation(libs.lwjgl.stb)
    implementation(libs.joml)
    implementation(libs.gson)
    runtimeOnly(libs.log4j.core)
}

val osClassifier = providers.provider {
    val os = System.getProperty("os.name").lowercase()
    val arch = System.getProperty("os.arch").lowercase()
    when {
        os.contains("win") && (arch.contains("arm64") || arch.contains("aarch64")) -> "windows-arm64"
        os.contains("win") && arch.contains("64") -> "windows-x64"
        os.contains("win") -> "windows-x86"
        os.contains("mac") && (arch.contains("arm64") || arch.contains("aarch64")) -> "macos-arm64"
        os.contains("mac") -> "macos-x64"
        os.contains("linux") && (arch.contains("arm64") || arch.contains("aarch64")) -> "linux-arm64"
        os.contains("linux") && arch.contains("64") -> "linux-x64"
        os.contains("linux") -> "linux-x86"
        else -> error("Unsupported OS: ${System.getProperty("os.name")} / $arch")
    }
}

val lwjglModules = listOf("lwjgl", "lwjgl-glfw", "lwjgl-opengl", "lwjgl-stb")

dependencies {
    lwjglModules.forEach {
        module ->runtimeOnly("org.lwjgl:$module") {
            artifact {
                classifier = "natives-" + when (osClassifier.get()) {
                    "windows-x64" -> "windows"
                    "macos-x64" -> "macos"
                    "linux-x64" -> "linux"
                    else -> osClassifier.get()
                }
            }
        }
    }
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveAppendix.set(project.name)
    archiveVersion.set(project.version.toString())
    archiveClassifier.set(osClassifier)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    transform<com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer>()
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs(
        "--enable-native-access=ALL-UNNAMED",
        "--add-exports=java.base/jdk.internal.misc=ALL-UNNAMED"
    )
}
