// server/build.gradle.kts

import org.gradle.api.file.DuplicatesStrategy

plugins {
    application
    alias(libs.plugins.shadow)
    id("net.grongubbe.java-conventions")
}

application {
    mainClass.set("net.grongubbe.tengoku.server.ServerMain")
}

dependencies {
    implementation(project(":common"))
    runtimeOnly(libs.log4j.core)
}

val platform = when {
    System.getProperty("os.name").lowercase().contains("windows") -> "windows"
    System.getProperty("os.name").lowercase().contains("mac") -> "macos"
    System.getProperty("os.name").lowercase().contains("linux") -> "linux"
    else -> error("Unsupported platform: ${System.getProperty("os.name")}")
}

tasks.shadowJar {
    archiveFileName.set("Tengoku-${project.name}-v${project.version}-${platform}.jar")

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    transform<com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer>()

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}