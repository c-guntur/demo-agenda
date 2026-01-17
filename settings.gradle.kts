// Derive the Gradle root project name from gradle.properties to avoid duplication
// Single source of truth: gradle.properties -> plugin.name
val pluginName = providers.gradleProperty("plugin.name").orNull
    ?: error("Missing 'plugin.name' in gradle.properties. Define it to set rootProject.name.")

rootProject.name = pluginName