// client/build.gradle.kts

import net.grongubbe.buildlogic.LwjglNativesExtension
import org.gradle.api.file.DuplicatesStrategy

plugins {
    application
    alias(libs.plugins.shadow)
    id("net.grongubbe.java-conventions")
    id("net.grongubbe.lwjgl-natives")
}

application {
    mainClass.set("net.grongubbe.tengoku.client.ClientMain")
    applicationDefaultJvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

val lwjglNatives = extensions.getByType(LwjglNativesExtension::class.java).classifier

dependencies {
    implementation(project(":common"))

    implementation(platform(libs.lwjgl.bom))
    implementation(libs.lwjgl)
    implementation(libs.lwjgl.glfw)
    implementation(libs.lwjgl.opengl)
    implementation(libs.lwjgl.stb)

    runtimeOnly(libs.log4j.core)

    runtimeOnly("org.lwjgl:lwjgl") { artifact { classifier = lwjglNatives } }
    runtimeOnly("org.lwjgl:lwjgl-glfw") { artifact { classifier = lwjglNatives } }
    runtimeOnly("org.lwjgl:lwjgl-opengl") { artifact { classifier = lwjglNatives } }
    runtimeOnly("org.lwjgl:lwjgl-stb") { artifact { classifier = lwjglNatives } }

    implementation(libs.joml)
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    mergeServiceFiles()
    transform<com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer>()

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
        attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    }
}