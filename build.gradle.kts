import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.18"
}

paperweight {
    upstreams.paper {
        ref = "6fb36e34b6bfc8442b8dbeb61cb82ad9ed39013b"

        patchFile {
            path = "paper-server/build.gradle.kts"
            outputFile = file("weeper-server/build.gradle.kts")
            patchFile = file("weeper-server/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-api/build.gradle.kts"
            outputFile = file("weeper-api/build.gradle.kts")
            patchFile = file("weeper-api/build.gradle.kts.patch")
        }
        patchFile {
            path = "paper-generator/build.gradle.kts"
            outputFile = file("weeper-generator/build.gradle.kts")
            patchFile = file("weeper-generator/build.gradle.kts.patch")
        }
        patchDir("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("weeper-api/paper-patches")
            outputDir = file("paper-api")
        }
        patchDir("paperGenerator") {
            upstreamPath = "paper-generator"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("weeper-generator/paper-patches")
            outputDir = file("paper-generator")
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
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
        options.release = 21
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
