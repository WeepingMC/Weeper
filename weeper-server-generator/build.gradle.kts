import org.spongepowered.gradle.vanilla.repository.MinecraftPlatform

plugins {
    java
    id("org.spongepowered.gradle.vanilla") version "0.2.1-SNAPSHOT"
}

minecraft {
    version(property("mcVersion").toString())
    platform(MinecraftPlatform.SERVER)

    runs {
        server("generate") {
            mainClass("io.papermc.generator.Main")
            accessWideners(projectDir.toPath().resolve("wideners.at"))
            args(projectDir.toPath().resolve("generated").toString())
            targetVersion(21)
        }
    }
}

dependencies {
    implementation("com.squareup:javapoet:1.13.0")
    implementation(project(":weeper-api"))
    implementation(project(":weeper-server"))
    implementation("io.github.classgraph:classgraph:4.8.168")
}

group = "io.papermc.paper"
version = "1.0-SNAPSHOT"

