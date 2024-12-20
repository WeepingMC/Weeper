import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG

var javaVersion = 21
var charSet: String = Charsets.UTF_8.name()

plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.patcher") version "1.7.7"
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = charSet
        options.release.set(javaVersion)
    }
    tasks.withType<Javadoc> {
        options.encoding = charSet
    }
    tasks.withType<ProcessResources> {
        filteringCharset = charSet
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }
}

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content {
            onlyForConfigurations(PAPERCLIP_CONFIG)
        }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.10.3:fat")
    decompiler("org.vineflower:vineflower:1.10.1")
    paperclip("io.papermc:paperclip:3.0.3")
}

paperweight {
    serverProject.set(project(":weeper-server"))

    remapRepo.set(paperMavenPublicUrl)
    decompileRepo.set(paperMavenPublicUrl)

    usePaperUpstream(providers.gradleProperty("upstreamRef")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("weeper-api"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("weeper-server"))
        }

        patchTasks {
            register("paperApiGenerator") {
                isBareDirectory.set(true)
                upstreamDirPath.set("paper-api-generator")
                patchDir.set(layout.projectDirectory.dir("patches/api-generator"))
                outputDir.set(layout.projectDirectory.dir("weeper-api-generator"))
            }
        }
    }
}

tasks.register("cleanup"){
    doLast{
        layout.projectDirectory.dir("weeper-api").asFile.deleteRecursively()
        layout.projectDirectory.dir("weeper-mojangapi").asFile.deleteRecursively()
        layout.projectDirectory.dir("weeper-server").asFile.deleteRecursively()
        layout.projectDirectory.dir("weeper-api-generator").asFile.deleteRecursively()
    }
}

tasks.generateDevelopmentBundle {
    apiCoordinates.set("com.github.weepingmc.weeper:weeper-api")
    libraryRepositories.addAll(
        "https://repo.maven.apache.org/maven2/",
        paperMavenPublicUrl
    )
}

allprojects {
    publishing {
        repositories {
            maven {
                authentication {
                    credentials(PasswordCredentials::class)
                }
                url = uri("https://dev.craftstuebchen.de/repo/repository/snapshots/")
                name = "cs"
            }
        }
    }
}


publishing {
    // Publishing dev bundle:
    // ./gradlew publishDevBundlePublicationTo(MavenLocal|MyRepoSnapshotsRepository) -PpublishDevBundle
    if (project.hasProperty("publishDevBundle")) {
        publications.create<MavenPublication>("devBundle") {
            artifact(tasks.generateDevelopmentBundle) {
                artifactId = "dev-bundle"
            }
        }
    }
}
