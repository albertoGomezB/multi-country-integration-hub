plugins {
  kotlin("jvm") version "2.0.10" apply false
  kotlin("plugin.spring") version "2.0.10" apply false

  id("org.springframework.boot") version "3.4.1" apply false
  id("io.spring.dependency-management") version "1.1.6" apply false
}

allprojects {
  group = "com.agb.integrationhub"
  version = "0.1.0"
  repositories { mavenCentral() }
}

subprojects {
  plugins.withId("org.jetbrains.kotlin.jvm") {
    extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension>("kotlin") {
      jvmToolchain(21)
    }
  }
}
