import io.papermc.paperweight.util.defaultJavaLauncher

plugins {
    java
    id("io.papermc.paperweight.source-generator")
}

paperweight {
    atFile.set(layout.projectDirectory.file("wideners.at"))
}

dependencies {
    minecraftJar(project(":weeper-server", "mappedJarOutgoing"))
    implementation(project(":weeper-server", "macheMinecraftLibraries"))

    implementation("com.squareup:javapoet:1.13.0")
    implementation(project(":weeper-api"))
    implementation("io.github.classgraph:classgraph:4.8.47")
    implementation("org.jetbrains:annotations:24.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<JavaExec>("generate") {
    dependsOn(tasks.check)
    mainClass.set("io.papermc.generator.Main")
    classpath(sourceSets.main.map { it.runtimeClasspath })
    args(rootProject.layout.projectDirectory.dir("weeper-server/src/generated/java").asFile.absolutePath)
    javaLauncher = javaToolchains.defaultJavaLauncher(project)
}

tasks.test {
    useJUnitPlatform()
}

group = "io.papermc.paper"
version = "1.0-SNAPSHOT"
