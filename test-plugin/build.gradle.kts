version = "1.0.0-SNAPSHOT"

dependencies {
    compileOnly(project(":weeper-api"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
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
