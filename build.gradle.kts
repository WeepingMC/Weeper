plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0" apply false
    id("io.papermc.paperweight.patcher") version "1.1.11"
    `maven-publish`
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            onlyForConfigurations("paperclip")
        }
    }
}

dependencies {
    remapper("org.quiltmc:tiny-remapper:0.4.3:fat")
    paperclip("io.papermc:paperclip:2.0.1")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(16))
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(16)
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}

paperweight {
    serverProject.set(project(":Weeper-Server"))

    useStandardUpstream("purpur") {
        url.set(github("pl3xgaming", "Purpur"))
        ref.set(providers.gradleProperty("upstreamRef"))

        withStandardPatcher {
            baseName("Purpur")

            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("Weeper-API"))

            remapRepo.set("https://maven.quiltmc.org/repository/release/")
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


subprojects {
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
