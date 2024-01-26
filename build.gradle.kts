import com.google.gradle.osdetector.OsDetector
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("java-library")
    id("maven-publish")
    id("com.google.osdetector")
    id("com.github.jmongard.git-semver-plugin")
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/MDL-Viewer/hott-voice")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
}

val os = osdetector.os
val os_platform = when (os) {
    "osx" -> "mac"
    "windows" -> "win"
    "linux" -> "linux"
    else -> throw UnsupportedOperationException("os $os is not supported")
}

dependencies {
    implementation("de.treichels.hott:hott-model:_")
    implementation("commons-io:commons-io:_")
    implementation("org.openjfx:javafx-base:_:$os_platform")
    implementation("no.tornado:tornadofx:_")

    testImplementation("junit:junit:_")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:_")
}

version = semver.version

tasks {
    jar {
        manifest {
            attributes (
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
            )
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/MDL-Viewer/hott-voice")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("github") {
            artifactId = project.name.lowercase()
            group = "de.treichels.hott"
            from(components["java"])
        }          
    }
}
