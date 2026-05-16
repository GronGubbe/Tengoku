package net.grongubbe.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.language.jvm.tasks.ProcessResources

class JavaConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.withPlugin("java") {
            project.extensions.getByType(JavaPluginExtension::class.java).toolchain.languageVersion.set(
                JavaLanguageVersion.of(25)
            )

            project.tasks.withType(JavaCompile::class.java).configureEach {
                options.encoding = "UTF-8"
                options.release.set(25)
            }

            project.tasks.withType(ProcessResources::class.java).configureEach {
                filteringCharset = "UTF-8"
            }

            project.tasks.withType(Jar::class.java).configureEach {
                isPreserveFileTimestamps = false
                isReproducibleFileOrder = true
            }

            project.tasks.withType(JavaExec::class.java).configureEach {
                standardInput = System.`in`
            }
        }
    }
}