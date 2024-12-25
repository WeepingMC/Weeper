version = "1.0.0-SNAPSHOT"


plugins {
    id("com.gradleup.shadow") version "9.0.0-beta4"
}

dependencies {
    compileOnly(project(":weeper-api"))
    implementation("com.github.alexdlaird:java-ngrok:2.3.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.2")
}

tasks.processResources {
    val apiVersion = rootProject.providers.gradleProperty("mcVersion").get()
        .split(".", "-").take(2).joinToString(".")
    val props = mapOf(
        "version" to project.version,
        "apiversion" to "\"$apiVersion\"",
    )
    inputs.properties(props)
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}


tasks.build {
    dependsOn(tasks.shadowJar)
}
