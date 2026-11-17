import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.bundling.Jar
import org.gradle.language.jvm.tasks.ProcessResources

allprojects {
    group = providers.gradleProperty("group").get()
    version = providers.gradleProperty("version").get()
}

subprojects {
    val targetJava = providers.gradleProperty("javaVersion").get().toInt()

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(targetJava)
    }

    tasks.withType<ProcessResources>().configureEach {
        filteringCharset = "UTF-8"
    }

    tasks.withType<Jar>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}