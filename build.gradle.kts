import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.19"
}

paperweight {
    upstreams.paper {
        ref.set(providers.gradleProperty("paperRef"))

        patchFile {
            path.set("paper-server/build.gradle.kts")
            outputFile.set(file("weeper-server/build.gradle.kts"))
            patchFile.set(file("weeper-server/build.gradle.kts.patch"))
        }
        patchFile {
            path.set("paper-api/build.gradle.kts")
            outputFile.set(file("weeper-api/build.gradle.kts"))
            patchFile.set(file("weeper-api/build.gradle.kts.patch"))
        }
        patchFile {
            path.set("paper-generator/build.gradle.kts")
            outputFile.set(file("weeper-generator/build.gradle.kts"))
            patchFile.set(file("weeper-generator/build.gradle.kts.patch"))
        }
        patchDir("paperApi") {
            upstreamPath.set("paper-api")
            excludes.set(setOf("build.gradle.kts"))
            patchesDir.set(file("weeper-api/paper-patches"))
            outputDir.set(file("paper-api"))
        }
        patchDir("paperGenerator") {
            upstreamPath.set("paper-generator")
            excludes.set(setOf("build.gradle.kts"))
            patchesDir.set(file("weeper-generator/paper-patches"))
            outputDir.set(file("paper-generator"))
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }

    dependencies {
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven("https://dev.craftstuebchen.de/repo/repository/snapshots/") {
                name = "cs"
                credentials(PasswordCredentials::class)
            }
        }
    }
}
