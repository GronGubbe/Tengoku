// common/build.gradle.kts

plugins {
    `java-library`
    id("net.grongubbe.java-conventions")
}

dependencies {
    api(libs.log4j.api)
    compileOnly(libs.jetbrains.annotations)
}