pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    mavenCentral()
  }
}

rootProject.name = "multi-country-integration-hub"

include(":libs:shared")
include(":services:ingest-api")
include(":services:consumer")

