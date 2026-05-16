// build-logic build.gradle.kts

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("javaConventions") {
            id = "net.grongubbe.java-conventions"
            implementationClass = "net.grongubbe.buildlogic.JavaConventionsPlugin"
        }
        create("lwjglNatives") {
            id = "net.grongubbe.lwjgl-natives"
            implementationClass = "net.grongubbe.buildlogic.LwjglNativesPlugin"
        }
    }
}