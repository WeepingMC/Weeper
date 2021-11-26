pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "Weeper"

include("Weeper-API", "Weeper-Server", "Weeper-MojangAPI")
