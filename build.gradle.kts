import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("io.papermc.paperweight.patcher") version "1.3.0"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    if (name == "Weeperr-MojangAPI") {
        return@subprojects
    }

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
    }

    apply<MavenPublishPlugin>()
    publishing {
        repositories {
            maven {
                authentication {
                    credentials(PasswordCredentials::class)
                }
                url = uri("https://nexus.endrealm.net/repository/toothpick/")
                name = "endrealm"
            }
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

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            onlyForConfigurations(PAPERCLIP_CONFIG)
        }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.7.0:fat")
    decompiler("net.minecraftforge:forgeflower:1.5.498.22")
    paperclip("io.papermc:paperclip:3.0.2-SNAPSHOT")
}

paperweight {
    serverProject.set(project(":Weeper-Server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    usePaperUpstream(providers.gradleProperty("upstreamRef")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("Weeper-API"))

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("Weeper-Server"))
        }

        patchTasks {
            register("mojangApi") {
                isBareDirectory.set(true)
                upstreamDirPath.set("Paper-MojangAPI")
                patchDir.set(layout.projectDirectory.dir("patches/mojangapi"))
                outputDir.set(layout.projectDirectory.dir("Weeper-MojangAPI"))
            }
        }
    }
}

tasks.register("cleanup"){
    doLast{
        layout.projectDirectory.dir("Weeper-API").asFile.deleteRecursively()
        layout.projectDirectory.dir("Weeper-MojangAPI").asFile.deleteRecursively()
        layout.projectDirectory.dir("Weeper-Server").asFile.deleteRecursively()
    }
}

tasks.generateDevelopmentBundle {
    apiCoordinates.set("com.github.weepingmc.weeper:Weeper-API")
    mojangApiCoordinates.set("com.github.weepingmc.weeper:Weeper-MojangAPI")
    libraryRepositories.addAll(
        "https://repo.maven.apache.org/maven2/",
        "https://libraries.minecraft.net/",
        "https://papermc.io/repo/repository/maven-public/",
        "https://maven.fabricmc.net/",
    )
}
