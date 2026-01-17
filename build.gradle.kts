plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "none.cgutils"
// Version is defined in gradle.properties as `version=...`

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Configure IntelliJ Platform Gradle Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    intellijPlatform {
        create("IC", "2025.1.4.1")
        // Use plain JUnit 5 for unit tests; platform integration tests are not required here

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }

    // JUnit 5 for unit tests

    // Use JUnit 5 BOM to manage versions centrally
    testImplementation(platform("org.junit:junit-bom:5.13.4"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // JUnit Platform suite support
    testRuntimeOnly("org.junit.platform:junit-platform-suite")
    testRuntimeOnly("org.junit.platform:junit-platform-suite-engine")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        // Set plugin name from gradle.properties -> plugin.displayName
        name.set(providers.gradleProperty("plugin.displayName"))

        // Read plugin description from README.adoc between <!-- DESC_BEGIN --> and <!-- DESC_END -->
        val descriptionProvider = providers.fileContents(layout.projectDirectory.file("README.adoc")).asText.map { text ->
            val start = "<!-- DESC_BEGIN -->"
            val end = "<!-- DESC_END -->"
            val startIdx = text.indexOf(start)
            val endIdx = text.indexOf(end)
            require(startIdx != -1 && endIdx != -1 && endIdx > startIdx) {
                "README.adoc must contain HTML description between $start and $end"
            }
            text.substring(startIdx + start.length, endIdx).trim()
        }

        description = descriptionProvider

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    // Enable JUnit 5 platform for tests
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed", "standardOut", "standardError")
            showStandardStreams = true
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
    }

    // Disable building Searchable Options index (speeds up local builds)
    named("buildSearchableOptions") {
        enabled = false
    }
    // And disable related packaging tasks for searchable options completely
    named("prepareJarSearchableOptions") {
        enabled = false
    }
    named("jarSearchableOptions") {
        enabled = false
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
