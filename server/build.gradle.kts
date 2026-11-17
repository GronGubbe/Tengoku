import org.gradle.api.file.DuplicatesStrategy

plugins {
    application
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("net.grongubbe.tengoku.server.ServerMain")
}

dependencies {
    implementation(project(":common"))
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
