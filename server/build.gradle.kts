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

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    transform<com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer>()

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}