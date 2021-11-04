import io.papermc.paperweight.util.constants.PAPERCLIP_CONFIG

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
    id("io.papermc.paperweight.patcher") version "1.1.14"
    `maven-publish`
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
    remapper("net.fabricmc:tiny-remapper:0.6.0:fat")
    decompiler("net.minecraftforge:forgeflower:1.5.498.12")
    paperclip("io.papermc:paperclip:2.0.1")
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }
}

subprojects {

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(16)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
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

paperweight {
    serverProject.set(project(":Weeper-Server"))

    usePaperUpstream(providers.gradleProperty("upstreamRef")) {
        withPaperPatcher {

            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("Weeper-API"))

            remapRepo.set("https://maven.fabricmc.net/")
            decompileRepo.set("https://files.minecraftforge.net/maven/")

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("Weeper-Server"))
        }
    }
}

tasks.register("cleanup"){
    doLast{
        layout.projectDirectory.dir("Weeper-API").asFile.deleteRecursively()
        layout.projectDirectory.dir("Weeper-Server").asFile.deleteRecursively()
    }
}

tasks.paperclipJar {
    destinationDirectory.set(rootProject.layout.projectDirectory)
    archiveFileName.set("weeper-paperclip.jar")
}
